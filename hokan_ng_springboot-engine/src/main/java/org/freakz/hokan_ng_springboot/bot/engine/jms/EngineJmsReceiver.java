package org.freakz.hokan_ng_springboot.bot.engine.jms;

import org.freakz.hokan_ng_springboot.bot.common.enums.HokanModule;
import org.freakz.hokan_ng_springboot.bot.common.jms.JmsEnvelope;
import org.freakz.hokan_ng_springboot.bot.common.jms.SpringJmsReceiver;
import org.freakz.hokan_ng_springboot.bot.common.jms.api.JmsServiceMessageHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by petria on 5.2.2015.
 */
@Component

public class EngineJmsReceiver extends SpringJmsReceiver {

    @Autowired
    private JmsServiceMessageHandler jmsServiceMessageHandler;


    @Override
    public String getDestinationName() {
        return HokanModule.HokanEngine.getQueueName();
    }

    @Override
    public void handleJmsEnvelope(JmsEnvelope envelope) throws Exception {
        jmsServiceMessageHandler.handleJmsEnvelope(envelope);
    }

}
