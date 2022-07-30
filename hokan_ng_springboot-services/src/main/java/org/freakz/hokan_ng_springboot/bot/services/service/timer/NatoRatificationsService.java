package org.freakz.hokan_ng_springboot.bot.services.service.timer;

import lombok.extern.slf4j.Slf4j;
import org.freakz.hokan_ng_springboot.bot.common.enums.HokanModule;
import org.freakz.hokan_ng_springboot.bot.common.events.NotifyRequest;
import org.freakz.hokan_ng_springboot.bot.common.events.ServiceRequest;
import org.freakz.hokan_ng_springboot.bot.common.events.ServiceRequestType;
import org.freakz.hokan_ng_springboot.bot.common.events.ServiceResponse;
import org.freakz.hokan_ng_springboot.bot.common.jms.api.JmsSender;
import org.freakz.hokan_ng_springboot.bot.common.jpa.entity.Channel;
import org.freakz.hokan_ng_springboot.bot.common.jpa.entity.PropertyName;
import org.freakz.hokan_ng_springboot.bot.common.jpa.service.ChannelPropertyService;
import org.freakz.hokan_ng_springboot.bot.common.jpa.service.DataValuesService;
import org.freakz.hokan_ng_springboot.bot.common.models.NatoRatifyStats;
import org.freakz.hokan_ng_springboot.bot.common.util.StaticStrings;
import org.freakz.hokan_ng_springboot.bot.services.service.annotation.ServiceMessageHandler;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class NatoRatificationsService {

    private static final String OTAN_STATS_KEY = "OTAN";
    private static final String OTAN_STATS_NICK = "_Hokan";
    private final ChannelPropertyService channelPropertyService;

    private final DataValuesService dataValuesService;

    private final JmsSender jmsSender;

    @Autowired
    public NatoRatificationsService(ChannelPropertyService channelPropertyService, DataValuesService dataValuesService, JmsSender jmsSender) {
        this.channelPropertyService = channelPropertyService;
        this.dataValuesService = dataValuesService;
        this.jmsSender = jmsSender;
    }


    public Elements fetchDataTable() {
        String url = "https://www.viastar.fi/laskuri/NATO-laskuri/en/";
        try {
            Document doc = Jsoup.connect(url).userAgent(StaticStrings.HTTP_USER_AGENT).get();
            Elements tableRows = doc.select("table.viastar").select("tr");
            return tableRows;
        } catch (IOException e) {
            log.error("OTAN data fetch failed", e);
        }

        return null;
    }

    public NatoRatifyStats fetchNatoData() {
        Elements elements = fetchDataTable();
        if (elements != null) {
            List<String> strings = elements.eachText();
            if (strings.size() == 34) {
                NatoRatifyStats stats = new NatoRatifyStats();
                for (int i = 1; i < 31; i++) {
                    String str = strings.get(i);
                    if (str.startsWith("X ")) {
                        stats.getNotRatified().add(str);
                    } else {
                        stats.getRatified().add(str);
                    }
                }


                stats.setSummary(strings.get(32));
                stats.setUpdated(strings.get(33));
                return stats;
            }
        }
        return null;
    }


    @ServiceMessageHandler(ServiceRequestType = ServiceRequestType.NATO_REQUEST)
    public void handleNatoNotifyCmdRequest(ServiceRequest request, ServiceResponse response) {
        NatoRatifyStats natoRatifyStats = fetchNatoData();
        response.setResponseData(request.getType().getResponseDataKey(), natoRatifyStats);
    }

    @Scheduled(fixedRate = 30 * 60 * 1000)
    public void notifyTimer() {
        sendNotify();
    }

    private List<String> sendNotify() {
        List<Channel> channelList = channelPropertyService.getChannelsWithProperty(PropertyName.PROP_CHANNEL_DO_OTAN, "true");
        List<String> notifySentTo = new ArrayList<>();
        for (Channel channel : channelList) {

            String channelName = channel.getChannelName();
            String network = channel.getNetwork().getName();

            NatoRatifyStats natoRatifyStats = fetchNatoData();
            if (natoRatifyStats != null) {
                String current = natoRatifyStats.toString();
                String stored = dataValuesService.getValue(OTAN_STATS_NICK, channelName, network, OTAN_STATS_KEY);
                if (!current.equals(stored)) {
                    NotifyRequest notifyRequest = new NotifyRequest();
                    notifyRequest.setNotifyMessage(current);
                    notifyRequest.setTargetChannelId(channel.getId());
                    notifySentTo.add(channel.getChannelName());
                    jmsSender.send(HokanModule.HokanServices, HokanModule.HokanIo.getQueueName(), "WHOLE_LINE_TRIGGER_NOTIFY_REQUEST", notifyRequest, false);
                    dataValuesService.setValue(OTAN_STATS_NICK, channelName, network, OTAN_STATS_KEY, current);
                    notifySentTo.add(channelName);
                }
            }
        }
        return notifySentTo;
    }

}
