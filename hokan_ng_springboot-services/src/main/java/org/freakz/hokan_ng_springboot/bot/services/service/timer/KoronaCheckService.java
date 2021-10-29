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
import org.freakz.hokan_ng_springboot.bot.services.service.annotation.ServiceMessageHandler;
import org.jibble.pircbot.Colors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class KoronaCheckService {

    private final ChannelPropertyService channelPropertyService;
    private final JmsSender jmsSender;
    private final KoronaJSONReader jsonReader;

    private Map<String, InfectedStats> ownerToStatsMap = new HashMap<>();
    private Integer dead = 0;
    private Integer inIcu = 0;
    private Integer inWard = 0;
    private Integer total = 0;

    @Autowired
    public KoronaCheckService(ChannelPropertyService channelPropertyService, JmsSender jmsSender, KoronaJSONReader jsonReader) {
        this.channelPropertyService = channelPropertyService;
        this.jmsSender = jmsSender;
        this.jsonReader = jsonReader;
    }

    private InfectedStats calcInfectedStatsDiffs(Integer currentDead, Integer currentInIcu, Integer currentInWard, Integer currentTotal, InfectedStats oldStats) {
        InfectedStats stats = new InfectedStats();
        stats.dead = Objects.requireNonNullElse(currentDead, 0);
        stats.inIcu = Objects.requireNonNullElse(currentInIcu, 0);
        stats.inWard = Objects.requireNonNullElse(currentInWard, 0);
        stats.total = Objects.requireNonNullElse(currentTotal, 0);

        if (stats.dead != oldStats.dead) {
            stats.deadDiff = stats.dead - oldStats.dead;
            stats.d0 = true;
            stats.d1 = true;
        }
        if (stats.inIcu != oldStats.inIcu) {
            stats.inIcuDiff = stats.inIcu - oldStats.inIcu;
            stats.d0 = true;
            stats.d2 = true;
        }
        if (stats.inWard != oldStats.inWard) {
            stats.inWardDiff = stats.inWard - oldStats.inWard;
            stats.d0 = true;
            stats.d3 = true;
        }
        if (stats.total != oldStats.total) {
            stats.totalDiff = stats.total - oldStats.total;
            stats.d0 = true;
            stats.d4 = true;
        }
        return stats;
    }

    @Scheduled(fixedRate = 60 * 1000)
    public void updateTimer() {
        updateCurrentStatsJSON();
    }

    private List<String> sendNotify(String notify) {
        List<Channel> channelList = channelPropertyService.getChannelsWithProperty(PropertyName.PROP_CHANNEL_DO_KORONA, "true");
        List<String> notifySentTo = new ArrayList<>();
        for (Channel channel : channelList) {
            NotifyRequest notifyRequest = new NotifyRequest();
            notifyRequest.setNotifyMessage(notify);
            notifyRequest.setTargetChannelId(channel.getId());
            notifySentTo.add(channel.getChannelName());
            jmsSender.send(HokanModule.HokanServices, HokanModule.HokanIo.getQueueName(), "WHOLE_LINE_TRIGGER_NOTIFY_REQUEST", notifyRequest, false);
        }
        return notifySentTo;
    }

    //    @Scheduled(fixedRate = 30 * 60 * 1000)
    public void notifyTimer() {
        InfectedStats oldStats = getInfectedStats("INTERNAL");
        InfectedStats ns = calcInfectedStatsDiffs(dead, inIcu, inWard, total, oldStats);
        setInfectedStats("INTERNAL", ns);
        if (ns.d0) {
            doNotify(ns);
        }
    }

    private List<String> doNotify(InfectedStats ns) {
        log.debug("c1: {} - c2: {} - c3: {} - c4: {} -- d1: {} - d2: {} - d3: {} - d4: {}", ns.dead, ns.inIcu, ns.inWard, ns.total, ns.deadDiff, ns.inIcuDiff, ns.inWardDiff, ns.totalDiff);
        String d1 = "";
        String d2 = "";
        String d3 = "";
        String d4 = "";

        String b1 = "";
        if (ns.d1) {
            b1 = Colors.BOLD;
        }
        String b2 = "";
        if (ns.d2) {
            b2 = Colors.BOLD;
        }
        String b3 = "";
        if (ns.d3) {
            b3 = Colors.BOLD;
        }
        String b4 = "";
        if (ns.d4) {
            b4 = Colors.BOLD;
        }

        String s1 = "+";
        if (ns.deadDiff < 0) {
            s1 = "";
        }
        String s2 = "+";
        if (ns.inIcuDiff < 0) {
            s2 = "";
        }
        String s3 = "+";
        if (ns.inWardDiff < 0) {
            s3 = "";
        }
        String s4 = "+";
        if (ns.totalDiff < 0) {
            s4 = "";
        }

        d1 = String.format(" - Dead: %d (%s%s%d%s)", ns.dead, b1, s1, ns.deadDiff, b1);

        d2 = String.format(" - InIcu: %d (%s%s%d%s)", ns.inIcu, b2, s2, ns.inIcuDiff, b2);

        d3 = String.format(" - InWard: %d (%s%s%d%s)", ns.inWard, b3, s3, ns.inWardDiff, b3);

        d4 = String.format(" - Total hospitalised: %d (%s%s%d%s)", ns.total, b4, s4, ns.totalDiff, b4);

        String notify = String.format("Korona update%s%s%s%s", d1, d2, d3, d4);
        log.debug("Korona notify: {}", notify);
        return sendNotify(notify);
    }

    @ServiceMessageHandler(ServiceRequestType = ServiceRequestType.KORONA_NOTIFY_REQUEST)
    public void handleKoronaNotifyCmdRequest(ServiceRequest request, ServiceResponse response) {
        log.debug("Handle KoronaNotifyCMD");
        InfectedStats oldStats = getInfectedStats("INTERNAL");
        InfectedStats ns = calcInfectedStatsDiffs(dead, inIcu, inWard, total, oldStats);
        setInfectedStats("INTERNAL", ns);
        List<String> strings = doNotify(ns);
        response.setResponseData(request.getType().getResponseDataKey(), strings);
    }


    @ServiceMessageHandler(ServiceRequestType = ServiceRequestType.KORONA_REQUEST)
    public void handleKoronaCmdRequest(ServiceRequest request, ServiceResponse response) {
        String nickId = (String) request.getParameters()[0];
        log.debug("Handle KoronaCMD: {}", nickId);
        InfectedStats oldStats = getInfectedStats(nickId);
        InfectedStats ns = calcInfectedStatsDiffs(dead, inIcu, inWard, total, oldStats);
        setInfectedStats(nickId, ns);
        Integer[] ret = {ns.dead, ns.inIcu, ns.inWard, ns.total, ns.deadDiff, ns.inIcuDiff, ns.inWardDiff, ns.totalDiff};
        response.setResponseData(request.getType().getResponseDataKey(), ret);
    }

    private InfectedStats getInfectedStats(String key) {
        InfectedStats stats = this.ownerToStatsMap.get(key);
        if (stats == null) {
            stats = new InfectedStats();
            stats.dead = Objects.requireNonNullElse(dead, 0);
            stats.inIcu = Objects.requireNonNullElse(inIcu, 0);
            stats.inWard = Objects.requireNonNullElse(inWard, 0);
            stats.total = Objects.requireNonNullElse(total, 0);
            this.ownerToStatsMap.put(key, stats);
        }
        return stats;
    }

    private void setInfectedStats(String key, InfectedStats newStats) {
        this.ownerToStatsMap.put(key, newStats);
    }

    private void updateCurrentStatsJSON() {
        Integer[] i = this.jsonReader.readKoronaJSON();
        if (i != null) {
            this.dead = i[0];
            this.inIcu = i[1];
            this.inWard = i[2];
            this.total = i[3];
        } else {
            log.error("Update failed!");
        }
    }

    public void test() {
//        notifyTimer();
        updateCurrentStatsJSON();
    }

    static class InfectedStats {
        int dead = 0;
        int inIcu = 0;
        int inWard = 0;
        int total = 0;

        int deadDiff = 0;
        int inIcuDiff = 0;
        int inWardDiff = 0;
        int totalDiff = 0;

        boolean d0 = false;

        boolean d1 = false;
        boolean d2 = false;
        boolean d3 = false;
        boolean d4 = false;
    }


}
