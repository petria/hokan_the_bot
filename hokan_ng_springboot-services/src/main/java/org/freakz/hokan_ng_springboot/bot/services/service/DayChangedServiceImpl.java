package org.freakz.hokan_ng_springboot.bot.services.service;

import org.freakz.hokan_ng_springboot.bot.common.cmdpool.CommandPool;
import org.freakz.hokan_ng_springboot.bot.common.cmdpool.CommandRunnable;
import org.freakz.hokan_ng_springboot.bot.common.enums.HokanModule;
import org.freakz.hokan_ng_springboot.bot.common.events.NotifyRequest;
import org.freakz.hokan_ng_springboot.bot.common.exception.HokanException;
import org.freakz.hokan_ng_springboot.bot.common.jms.api.JmsSender;
import org.freakz.hokan_ng_springboot.bot.common.jpa.entity.Channel;
import org.freakz.hokan_ng_springboot.bot.common.jpa.entity.PropertyName;
import org.freakz.hokan_ng_springboot.bot.common.jpa.entity.Url;
import org.freakz.hokan_ng_springboot.bot.common.jpa.service.ChannelPropertyService;
import org.freakz.hokan_ng_springboot.bot.common.jpa.service.PropertyService;
import org.freakz.hokan_ng_springboot.bot.common.jpa.service.UrlLoggerService;
import org.freakz.hokan_ng_springboot.bot.common.models.StatsData;
import org.freakz.hokan_ng_springboot.bot.common.models.StatsMapper;
import org.freakz.hokan_ng_springboot.bot.common.service.StatsService;
import org.freakz.hokan_ng_springboot.bot.common.util.StringStuff;
import org.freakz.hokan_ng_springboot.bot.common.util.TimeUtil;
import org.freakz.hokan_ng_springboot.bot.services.service.nimipaiva.NimipaivaService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.*;

/**
 * Created by Petri Airio on 22.9.2015.
 * -
 */
@Service
public class DayChangedServiceImpl implements DayChangedService, CommandRunnable {

    private static final Logger log = LoggerFactory.getLogger(DayChangedServiceImpl.class);

    private final ChannelPropertyService channelPropertyService;

    private final CommandPool commandPool;

    private final JmsSender jmsSender;

    private final NimipaivaService nimipaivaService;

    private final PropertyService propertyService;

    private final StatsService statsService;

    private final UrlLoggerService urlLoggerService;

    private Map<String, String> daysDone = new HashMap<>();

    @Autowired
    public DayChangedServiceImpl(ChannelPropertyService channelPropertyService, CommandPool commandPool, JmsSender jmsSender, NimipaivaService nimipaivaService, PropertyService propertyService, StatsService statsService, UrlLoggerService urlLoggerService) {
        this.channelPropertyService = channelPropertyService;
        this.commandPool = commandPool;
        this.jmsSender = jmsSender;
        this.nimipaivaService = nimipaivaService;
        this.propertyService = propertyService;
        this.statsService = statsService;
        this.urlLoggerService = urlLoggerService;
    }

    public DayChangedServiceImpl() {

        channelPropertyService = null;
        urlLoggerService = null;
        statsService = null;
        propertyService = null;
        commandPool = null;
        jmsSender = null;
        nimipaivaService = null;
    }

    @PostConstruct
    private void startRunner() {
        commandPool.startRunnable(this, "<system>");
    }

    @Override
    public void handleRun(long myPid, Object args) throws HokanException {
        while (true) {
            try {
                checkDayChanged();
                Thread.sleep(10 * 1000);
            } catch (Exception e) {
                log.error("Error", e);
                break;
            }
        }
    }

    private String getDailyStats(Channel channel) {
        LocalDateTime yesterday = LocalDateTime.now().minusDays(1);
        StatsMapper statsMapper = statsService.getDailyStatsForChannel(yesterday, channel.getChannelName());

        if (!statsMapper.hasError()) {
            List<StatsData> statsDatas = statsMapper.getStatsData();
            ZonedDateTime zdt = ZonedDateTime.of(yesterday, ZoneId.systemDefault());
            GregorianCalendar cal = GregorianCalendar.from(zdt); // TODO

            String res = StringStuff.formatTime(cal.getTime(), StringStuff.STRING_STUFF_DF_DDMMYYYY) + " word stats:";
            int i = 1;
            int countTotal = 0;
            for (StatsData statsData : statsDatas) {
                res += " " + i + ") " + statsData.getNick() + "=" + statsData.getWords();
                countTotal += statsData.getWords();
                i++;
            }
            res += " - Word count = " + countTotal;
            return res;
        } else {
            log.warn("Could not create stats notify request!");
        }
        return null;
    }

    private void checkDayChanged() {
        String dayChangedTime = propertyService.getPropertyAsString(PropertyName.PROP_SYS_DAY_CHANGED_TIME, "00:00");
        String time = StringStuff.formatTime(new Date(), StringStuff.STRING_STUFF_DF_HHMM);
        if (time.equals(dayChangedTime)) {
            String dayCheck = StringStuff.formatTime(new Date(), StringStuff.STRING_STUFF_DF_DDMMYYYY);
            boolean dayDone = daysDone.containsKey(dayCheck);
            if (!dayDone) {
                if (handleDayChangedTo(dayCheck)) {
                    daysDone.put(dayCheck, "done");
                }
            }
        }
    }

    private boolean handleDayChangedTo(String dayChangedTo) {
        log.debug("Day changed to: {}", dayChangedTo);
        List<Channel> channelList = channelPropertyService.getChannelsWithProperty(PropertyName.PROP_CHANNEL_DO_DAY_CHANGED, ".*");
        for (Channel channel : channelList) {
            String property = channelPropertyService.getChannelPropertyAsString(channel, PropertyName.PROP_CHANNEL_DO_DAY_CHANGED, "");

            String topic = "";
            if (parseProperty(property, "topic") != null) {
                String dailyNimip = getNimipäivät();

                LocalDate date = LocalDate.now();
                TemporalField woy = WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear();
                int weekNumber = date.get(woy);
//                int week = LocalDateTime.now(). DateTime.now().getWeekOfWeekyear();
                topic = String.format("---=== Day changed to: %s (W%d) :: %s ===---", dayChangedTo, weekNumber, dailyNimip);
            }

            String sunRises = "";
            List<String> sunRisesCities = parseProperty(property, "sunRises");
            if (sunRisesCities != null) {
                sunRises = getSunriseTexts(sunRisesCities);
            }
            String dailyStats = "";
            if (parseProperty(property, "dailyStats") != null) {
                dailyStats = getDailyStats(channel);
            }
            String dailyUrls = "";
            if (parseProperty(property, "dailyUrls") != null) {
                dailyUrls = getDailyUrls(channel.getChannelName());
            }

            NotifyRequest notifyRequest = new NotifyRequest();
            notifyRequest.setNotifyMessage(String.format("%s\n%s\n%s\n%s", topic, sunRises, dailyStats, dailyUrls));
            notifyRequest.setTargetChannelId(channel.getId());
            jmsSender.send(HokanModule.HokanServices, HokanModule.HokanIo.getQueueName(), "WHOLE_LINE_TRIGGER_NOTIFY_REQUEST", notifyRequest, false);
        }
        return true;
    }

    private List<String> parseProperty(String property, String keyword) {
        String[] split = property.split(" ");
        for (String str : split) {
            if (str.startsWith(keyword)) {
                if (str.contains(":")) {
                    String[] split2 = str.split(":");
                    List<String> list = new LinkedList<>(Arrays.asList(split2));
                    if (list.size() > 1) {
                        list.remove(0);
                    }
                    return list;
                } else {
                    return new ArrayList<>();
                }
            }
        }
        return null;
    }


    public String getSunriseTexts(List<String> sunRisesCities) {
        String ret = null;
        String baseUrl = "http://en.ilmatieteenlaitos.fi/weather/";

        for (String city : sunRisesCities) {
            String url = baseUrl + city;
            Document doc;
            try {
                doc = Jsoup.connect(url).get();
            } catch (IOException e) {
                continue;
            }
            if (ret == null) {
                ret = "";
            } else {
                ret += "\n";
            }
            Elements elements1 = doc.getElementsByAttributeValue("class", "col-xs-12");
            String place = elements1.get(1).text().split(" ")[2];
            ret += place + ": ";

            Elements elementsT = doc.getElementsByAttributeValue("class", "celestial-status-text");
            String tt = elementsT.get(1).text();
            ret += tt;
        }
        return ret;
    }

    private String getNimipäivät() {
        List<String> nimiPvmList = nimipaivaService.getNamesForDay(LocalDateTime.now()).getNames();
        String ret = "";
        for (String nimi : nimiPvmList) {
            if (ret.length() > 0) {
                ret += ", ";
            }
            ret += nimi;
        }
        return ret;
    }

    private String getDailyUrls(String channelName) {
        LocalDateTime time = LocalDateTime.now().minusDays(1);
        List counts = urlLoggerService.findTopSenderByChannelAndCreatedBetween(channelName, TimeUtil.getStartAndEndTimeForDay(time));

        ZonedDateTime zdt = ZonedDateTime.of(time, ZoneId.systemDefault());
        GregorianCalendar cal = GregorianCalendar.from(zdt); // TODO
        String ret = StringStuff.formatTime(cal.getTime(), StringStuff.STRING_STUFF_DF_DDMMYYYY) + " url  stats: ";

        if (counts.size() == 0) {
            ret += "no urls!!";
            return ret;
        }
        int max_count = 9;
        for (int i = 0; i < counts.size(); i++) {
            Object[] counter = (Object[]) counts.get(i);
            Url url = (Url) counter[0];
            Long count = (Long) counter[1];
            if (i > 0) {
                ret += ", ";
            }
            ret += (i + 1) + ") " + url.getSender() + "=" + count;
            if (i == max_count) {
                break;
            }
        }
        long countTotal = 0;
        for (Object count1 : counts) {
            Object[] counter = (Object[]) count1;
            Long count = (Long) counter[1];
            countTotal += count;
        }
        ret += " - Url count = " + countTotal;

        return ret;
    }

}
