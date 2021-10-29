package org.freakz.hokan_ng_springboot.bot.io.jms;

import org.freakz.hokan_ng_springboot.bot.common.enums.HokanModule;
import org.freakz.hokan_ng_springboot.bot.common.events.EngineResponse;
import org.freakz.hokan_ng_springboot.bot.common.events.NotifyRequest;
import org.freakz.hokan_ng_springboot.bot.common.jms.JmsEnvelope;
import org.freakz.hokan_ng_springboot.bot.common.jms.SpringJmsReceiver;
import org.freakz.hokan_ng_springboot.bot.common.service.ConnectionManagerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by petria on 5.2.2015.
 * -
 */
@Component
public class IoJmsReceiver extends SpringJmsReceiver {

    private static final Logger log = LoggerFactory.getLogger(IoJmsReceiver.class);

    private final ConnectionManagerService connectionManagerService;

    @Autowired
    public IoJmsReceiver(ConnectionManagerService connectionManagerService) {
        this.connectionManagerService = connectionManagerService;
    }

    @Override
    public String getDestinationName() {
        return HokanModule.HokanIo.getQueueName();
    }

    @Override
    public void handleJmsEnvelope(JmsEnvelope envelope) throws Exception {
        log.debug("handle!");
        if (envelope.getMessageIn().getPayLoadObject("ENGINE_RESPONSE") != null) {
            handleEngineReply(envelope);
        } else if (envelope.getMessageIn().getPayLoadObject("TELEGRAM_NOTIFY_REQUEST") != null) {
            handleNotify(envelope, "TELEGRAM_NOTIFY_REQUEST");
        } else if (envelope.getMessageIn().getPayLoadObject("TV_NOTIFY_REQUEST") != null) {
            handleNotify(envelope, "TV_NOTIFY_REQUEST");
        } else if (envelope.getMessageIn().getPayLoadObject("STATS_NOTIFY_REQUEST") != null) {
            handleNotify(envelope, "STATS_NOTIFY_REQUEST");
        } else if (envelope.getMessageIn().getPayLoadObject("URLS_NOTIFY_REQUEST") != null) {
            handleNotify(envelope, "URLS_NOTIFY_REQUEST");
        } else if (envelope.getMessageIn().getPayLoadObject("WHOLE_LINE_TRIGGER_NOTIFY_REQUEST") != null) {
            handleNotify(envelope, "WHOLE_LINE_TRIGGER_NOTIFY_REQUEST");
        } else if (envelope.getMessageIn().getPayLoadObject("TORRENT_NOTIFY_REQUEST") != null) {
            handleNotify(envelope, "TORRENT_NOTIFY_REQUEST");
        } else {
            log.debug("Not handled: {}", envelope);
        }

    }

    private void handleNotify(JmsEnvelope envelope, String payload) {
        NotifyRequest notifyRequest = (NotifyRequest) envelope.getMessageIn().getPayLoadObject(payload);
        notifyRequest.setNotifyType(payload);
        log.debug("handling NotifyRequest: {}", notifyRequest);
        connectionManagerService.handleNotifyRequest(notifyRequest);
    }

    private void handleEngineReply(JmsEnvelope envelope) {
        EngineResponse response = (EngineResponse) envelope.getMessageIn().getPayLoadObject("ENGINE_RESPONSE");
        connectionManagerService.handleEngineResponse(response);
    }

}
