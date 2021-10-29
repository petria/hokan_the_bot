package org.freakz.hokan_ng_springboot.bot.io.telegram.jms;

import org.freakz.hokan_ng_springboot.bot.common.enums.HokanModule;
import org.freakz.hokan_ng_springboot.bot.common.events.EngineResponse;
import org.freakz.hokan_ng_springboot.bot.common.events.IrcMessageEvent;
import org.freakz.hokan_ng_springboot.bot.common.events.MessageToTelegram;
import org.freakz.hokan_ng_springboot.bot.common.jms.JmsEnvelope;
import org.freakz.hokan_ng_springboot.bot.common.jms.SpringJmsReceiver;
import org.freakz.hokan_ng_springboot.bot.io.telegram.service.TelegramConnectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by petria on 5.2.2015.
 * -
 */
@Component
public class IoTelegramJmsReceiver extends SpringJmsReceiver {


    @Autowired
    private TelegramConnectService telegramConnectService;

    @Override
    public String getDestinationName() {
        return HokanModule.HokanIoTelegram.getQueueName();
    }

    @Override
    public void handleJmsEnvelope(JmsEnvelope envelope) throws Exception {
        if (envelope.getMessageIn().getPayLoadObject("ENGINE_RESPONSE") != null) {
            handleEngineReply(envelope);
        } else if (envelope.getMessageIn().getPayLoadObject("IRC_MESSAGE") != null) {
            IrcMessageEvent event = (IrcMessageEvent) envelope.getMessageIn().getPayLoadObject("IRC_MESSAGE");
            telegramConnectService.handleIrcMessage(event);
        } else if (envelope.getMessageIn().getPayLoadObject("MESSAGE_TO_TELEGRAM") != null) {
            MessageToTelegram message = (MessageToTelegram) envelope.getMessageIn().getPayLoadObject("MESSAGE_TO_TELEGRAM");
            telegramConnectService.handleMessageToTelegram(message);
        }
    }


    private void handleEngineReply(JmsEnvelope envelope) {
        EngineResponse response = (EngineResponse) envelope.getMessageIn().getPayLoadObject("ENGINE_RESPONSE");
        telegramConnectService.handleEngineResponse(response);
    }

}
