package org.freakz.hokan_ng_springboot.bot.services.jms;

import org.freakz.hokan_ng_springboot.bot.common.enums.HokanModule;
import org.freakz.hokan_ng_springboot.bot.common.jms.JmsEnvelope;
import org.freakz.hokan_ng_springboot.bot.common.jms.SpringJmsReceiver;
import org.freakz.hokan_ng_springboot.bot.common.jms.api.JmsSender;
import org.freakz.hokan_ng_springboot.bot.common.jms.api.JmsServiceMessageHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by petria on 5.2.2015.
 * -
 */
@Component
public class ServicesJmsReceiver extends SpringJmsReceiver {

    private static final Logger log = LoggerFactory.getLogger(ServicesJmsReceiver.class);

    private final JmsSender jmsSender;

    private final JmsServiceMessageHandler jmsServiceMessageHandler;

    @Autowired
    public ServicesJmsReceiver(JmsSender jmsSender, JmsServiceMessageHandler jmsServiceMessageHandler) {
        this.jmsSender = jmsSender;
        this.jmsServiceMessageHandler = jmsServiceMessageHandler;
    }


    @Override
    public String getDestinationName() {
        return HokanModule.HokanServices.getQueueName();
    }


    @Override
    public void handleJmsEnvelope(JmsEnvelope envelope) throws Exception {
        jmsServiceMessageHandler.handleJmsEnvelope(envelope);
    }

}
