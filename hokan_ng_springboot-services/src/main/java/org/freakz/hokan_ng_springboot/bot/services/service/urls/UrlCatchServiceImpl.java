package org.freakz.hokan_ng_springboot.bot.services.service.urls;

import org.freakz.hokan_ng_springboot.bot.common.enums.HokanModule;
import org.freakz.hokan_ng_springboot.bot.common.events.IrcMessageEvent;
import org.freakz.hokan_ng_springboot.bot.common.events.NotifyRequest;
import org.freakz.hokan_ng_springboot.bot.common.events.ServiceRequest;
import org.freakz.hokan_ng_springboot.bot.common.events.UrlCatchResolvedEvent;
import org.freakz.hokan_ng_springboot.bot.common.jms.api.JmsSender;
import org.freakz.hokan_ng_springboot.bot.common.jpa.entity.Channel;
import org.freakz.hokan_ng_springboot.bot.common.jpa.entity.Network;
import org.freakz.hokan_ng_springboot.bot.common.jpa.entity.PropertyName;
import org.freakz.hokan_ng_springboot.bot.common.jpa.entity.Url;
import org.freakz.hokan_ng_springboot.bot.common.jpa.repository.UrlRepository;
import org.freakz.hokan_ng_springboot.bot.common.jpa.service.ChannelPropertyRepositoryService;
import org.freakz.hokan_ng_springboot.bot.common.jpa.service.ChannelService;
import org.freakz.hokan_ng_springboot.bot.common.jpa.service.NetworkService;
import org.freakz.hokan_ng_springboot.bot.common.util.StaticStrings;
import org.freakz.hokan_ng_springboot.bot.common.util.StringStuff;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Petri Airio on 27.8.2015.
 * -
 */
@Service
public class UrlCatchServiceImpl implements UrlCatchService {

    private static final Logger log = LoggerFactory.getLogger(UrlCatchServiceImpl.class);

    private final ChannelPropertyRepositoryService channelPropertyService;

    private final ChannelService channelService;

    private final JmsSender jmsSender;

    private final NetworkService networkService;

    private final UrlRepository urlRepository;

    @Autowired
    public UrlCatchServiceImpl(ChannelPropertyRepositoryService channelPropertyService, ChannelService channelService, JmsSender jmsSender, NetworkService networkService, UrlRepository urlRepository) {
        this.channelPropertyService = channelPropertyService;
        this.channelService = channelService;
        this.jmsSender = jmsSender;
        this.networkService = networkService;
        this.urlRepository = urlRepository;
    }

    @Override
    public void catchUrls(ServiceRequest request) {
        IrcMessageEvent ircMessageEvent = request.getIrcMessageEvent();
        Network network = networkService.getNetwork(ircMessageEvent.getNetwork());
        Channel channel = channelService.findByNetworkAndChannelName(network, ircMessageEvent.getChannel());
        Object[] objs = request.getParameters();
        if (objs != null && objs.length > 0) {
            catchUrlAlreadySolved(ircMessageEvent, objs[0], channel);
        } else {
            catchUrls(ircMessageEvent, channel, false);
        }
    }

    private void catchUrlAlreadySolved(IrcMessageEvent ircMessageEvent, Object parameter, Channel channel) {
        UrlCatchResolvedEvent event = (UrlCatchResolvedEvent) parameter;
        long isWanha = logUrl(ircMessageEvent, event.getUrl());

        doUrl(isWanha, event.getUrl(), ignoreTitles, channel, false, true, event.getTitle());

        log.debug("Already titled url {} -> {}", event.getUrl(), event.getTitle());
    }

    private long logUrl(IrcMessageEvent iEvent, String url) {
        long isWanha = 0;

        Url entity = urlRepository.findFirstByUrlLikeOrUrlTitleLikeOrderByCreatedDesc(url, url);
        if (entity != null) {
            entity.addWanhaCount(1);
            isWanha = entity.getWanhaCount();
        } else {
            entity = new Url(url, iEvent.getSender(), iEvent.getChannel(), new Date());
        }

        log.info("Saving URL: {}", entity);
        Url saved = urlRepository.save(entity);
        log.info("Logged URL: {}", saved);
        return isWanha;

    }

    private static String ignoreTitles = "fbcdn-sphotos.*|.*(avi|bz|gz|gif|exe|iso|jpg|jpeg|mp3|mp4|mkv|mpeg|mpg|mov|pdf|png|torrent|zip|7z|tar)";

    private void catchUrls(IrcMessageEvent iEvent, Channel channel, boolean isNewTitle) {
        String msg = iEvent.getMessage();
        boolean askWanha = false;
        if (msg.contains("wanha?")) {
            askWanha = true;
        }

        String regexp = "(https?://|www\\.)\\S+";

        Pattern p = Pattern.compile(regexp, Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(msg);
        while (m.find()) {
            String url = m.group();
            long isWanha = logUrl(iEvent, url);
            doUrl(isWanha, url, ignoreTitles, channel, askWanha, isNewTitle, null);

        }
    }

    private void doUrl(long isWanha, String url, String ignoreTitles, Channel channel, boolean askWanha, boolean isNewTitle, String title) {
        StringBuilder wanhaAdd = new StringBuilder();
        for (int i = 0; i < isWanha; i++) {
            wanhaAdd.append("!");
        }
        if (!isNewTitle && !StringStuff.match(url, ignoreTitles, true)) {
            log.info("Finding title: " + url);
            getTitleNew(url, channel, isWanha > 0, wanhaAdd.toString());
        } else {
            log.info("Using already solver title: " + title);
            boolean titles = channelPropertyService.getChannelPropertyAsBoolean(channel, PropertyName.PROP_CHANNEL_DO_URL_TITLES, false);
            if (channel.getChannelName().contains("privmsg") || titles) {
                NotifyRequest notifyRequest = new NotifyRequest();
                notifyRequest.setTargetChannelId(channel.getId());
                if (isWanha > 0) {
                    if (askWanha) {
                        notifyRequest.setNotifyMessage(title + " | joo oli wanha!");
                    } else {
                        notifyRequest.setNotifyMessage(title + " | wanha" + wanhaAdd);
                    }
                } else {
                    notifyRequest.setNotifyMessage(title);
                }
                jmsSender.send(HokanModule.HokanServices, HokanModule.HokanIo.getQueueName(), "URLS_NOTIFY_REQUEST", notifyRequest, false);

            }
        }


    }

    private String getIMDBData(String url) throws Exception {
        org.jsoup.nodes.Document doc = Jsoup.connect(url).userAgent(StaticStrings.HTTP_USER_AGENT).get();
        String rating = doc.getElementsByAttributeValue("itemprop", "ratingValue").get(0).text();
        String users = doc.getElementsByAttributeValue("itemprop", "ratingCount").get(0).text();
        return String.format("Ratings: %s/10 from %s users", rating, users);
    }

    private void getTitleNew(final String url, final Channel ch, final boolean isWanha, final String wanhaAadd) {
        String title = null;

        if (url.contains("www.youtube.com") || url.contains("://youtu.be/")) {
            org.jsoup.nodes.Document doc;
            try {
                doc = Jsoup.connect(url).get();
                Elements scripts = doc.getElementsByTag("script");
                for (Element element : scripts) {
                    String text = element.html();
                    System.out.printf("%s", text);
                    if (text.contains("var ytplayer =")) {
                        int idx1 = text.indexOf(",\"title\":\"");
                        if (idx1 != -1) {
                            int idx2 = text.indexOf("\"", idx1 + 10);
                            if (idx2 != -1) {
                                title = text.substring(idx1 + 10, idx2);
                            }
                        }
                    }
                }
            } catch (IOException e) {
                //
            }

        } else {
            org.jsoup.nodes.Document doc;
            try {
                doc = Jsoup.connect(url).get();
            } catch (IOException e) {
                log.error("Can't get title for: {}", url);
                return;
            }
            Elements titleElements = doc.getElementsByTag("title");
            if (titleElements.size() > 0) {
                title = titleElements.get(0).text();
                title = title.replaceAll("[\n\t]", "");
            }

        }


        if (title != null) {
            title = StringStuff.htmlEntitiesToText(title);
            title = title.replaceAll("\t", "");

            log.debug("title: '{}'", title);

            Url entity = urlRepository.findFirstByUrlLikeOrUrlTitleLikeOrderByCreatedDesc(url, url);
            entity.setUrlTitle(title);
            urlRepository.save(entity);
            if (title.length() == 0) {
                return;
            }

            if (url.contains("http://www.imdb.com/title/")) {
                try {
                    String ratings = getIMDBData(url);
                    if (ratings != null) {
                        title += " | " + ratings;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (isWanha) {
                title = title + " | wanha" + wanhaAadd;
            }
            boolean titles = channelPropertyService.getChannelPropertyAsBoolean(ch, PropertyName.PROP_CHANNEL_DO_URL_TITLES, false);
            if (titles) {
                NotifyRequest notifyRequest = new NotifyRequest();
                notifyRequest.setNotifyMessage(title);
                notifyRequest.setTargetChannelId(ch.getId());
                jmsSender.send(HokanModule.HokanServices, HokanModule.HokanIo.getQueueName(), "URLS_NOTIFY_REQUEST", notifyRequest, false);
            }
        } else {
            log.info("Could not find title for url: " + url);
        }
    }

}
