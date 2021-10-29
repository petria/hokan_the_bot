package org.freakz.hokan_ng_springboot.bot.common.jms;

import org.freakz.hokan_ng_springboot.bot.common.enums.HokanModule;
import org.freakz.hokan_ng_springboot.bot.common.jms.api.JmsSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.InvalidDestinationException;
import org.springframework.jms.UncategorizedJmsException;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.ObjectMessage;

/**
 * Created by petria on 6.2.2015.
 */
@Component
//@Profile("default")
public class SpringJmsSender implements JmsSender {

    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    private JmsStatsHandler jmsStatsHandler;

    public ObjectMessage sendAndGetReply(String destination, String key, Object object, boolean deliveryPersistent) {
//    log.debug("{}: {} -> {}", destination, key, object);
        this.jmsTemplate.setReceiveTimeout(60 * 1000);
        this.jmsTemplate.setDeliveryPersistent(deliveryPersistent);
        Message reply = this.jmsTemplate.sendAndReceive(destination, session -> {
                    ObjectMessage objectMessage = session.createObjectMessage();
                    JmsMessage jmsMessage = new JmsMessage();
                    jmsMessage.addPayLoadObject(key, object);
                    objectMessage.setObject(jmsMessage);
                    return objectMessage;
                }
        );
        jmsStatsHandler.messageSent(destination);
        return (ObjectMessage) reply;
    }


    public void send(HokanModule hokanModule, String destination, String key, Object object, boolean deliveryPersistent) {
//    log.debug("{}: {} -> {}", destination, key, object);
        jmsStatsHandler.messageSent(destination);
        this.jmsTemplate.setDeliveryPersistent(deliveryPersistent);
        this.jmsTemplate.send(destination, session -> {
                    ObjectMessage objectMessage = session.createObjectMessage();

                    JmsMessage jmsMessage = new JmsMessage();
                    jmsMessage.setSender(hokanModule);
                    jmsMessage.addPayLoadObject(key, object);
                    objectMessage.setObject(jmsMessage);
                    return objectMessage;
                }
        );
    }

    public void sendJmsMessage(Destination destination, JmsMessage jmsMessage) {
//    log.debug("{}: ", destination);
        jmsStatsHandler.messageSent(destination.toString());
        try {
            this.jmsTemplate.send(destination, session -> {
                ObjectMessage objectMessage = session.createObjectMessage();
                objectMessage.setObject(jmsMessage);
                return objectMessage;
            });
        } catch (InvalidDestinationException | UncategorizedJmsException ex) {
            // ignore
        }
    }

}
