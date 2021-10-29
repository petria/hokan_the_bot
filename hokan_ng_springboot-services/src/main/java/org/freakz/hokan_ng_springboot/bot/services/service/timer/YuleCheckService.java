package org.freakz.hokan_ng_springboot.bot.services.service.timer;

import lombok.extern.slf4j.Slf4j;
import org.freakz.hokan_ng_springboot.bot.common.enums.HokanModule;
import org.freakz.hokan_ng_springboot.bot.common.events.NotifyRequest;
import org.freakz.hokan_ng_springboot.bot.common.jms.api.JmsSender;
import org.freakz.hokan_ng_springboot.bot.common.jpa.entity.Channel;
import org.freakz.hokan_ng_springboot.bot.common.jpa.entity.PropertyName;
import org.freakz.hokan_ng_springboot.bot.common.jpa.service.ChannelPropertyService;
import org.freakz.hokan_ng_springboot.bot.common.jpa.service.DataValuesService;
import org.freakz.hokan_ng_springboot.bot.common.jpa.service.PropertyService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class YuleCheckService {

    private final ChannelPropertyService channelPropertyService;
    private final DataValuesService dataValuesService;
    private final JmsSender jmsSender;
    private final PropertyService propertyService;


    public YuleCheckService(ChannelPropertyService channelPropertyService, DataValuesService dataValuesService, JmsSender jmsSender, PropertyService propertyService) {
        this.channelPropertyService = channelPropertyService;
        this.dataValuesService = dataValuesService;
        this.jmsSender = jmsSender;
        this.propertyService = propertyService;
    }

    @Scheduled(initialDelay = 10000, fixedDelay = 30000)
    public void checkIsItYule() {
        String yule = getYuleTime();
        if (yule == null) {
            log.warn("Yule time not set!");
            return;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy");
        String year = formatter.format(LocalDateTime.now());

        String now = getNow();
        if (now.equals(yule)) {
            boolean isSent = isYuleSent(year);
            if (!isSent) {
                String msg = String.format("Happy Yule at %s ", yule);
                sendNotify(msg);
                markYuleSent(year);
            }
        }
    }

    private String getYuleTime() {
        return propertyService.getPropertyAsString(PropertyName.PROP_SYS_YULE_TIME, null);
    }

    private void markYuleSent(String year) {
        log.debug("Setting Yule sent for year: {}", year);
        dataValuesService.setValue("_Hokan_", "_Hokan_", "_Hokan_", "Yule" + year, "SENT!");
    }

    private boolean isYuleSent(String year) {
        String value = dataValuesService.getValue("_Hokan_", "_Hokan_", "_Hokan_", "Yule" + year);
        if (value == null) {
            return false;
        }
        return true;
    }

    private String getNow() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-dd-MM HH:mm");
        String formatDateTime = now.format(formatter);
        return formatDateTime;
//        return "2020-21-12 10:02";
    }

    private List<String> sendNotify(String notify) {
        List<Channel> channelList = channelPropertyService.getChannelsWithProperty(PropertyName.PROP_CHANNEL_DO_YULE, "true");
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

}
