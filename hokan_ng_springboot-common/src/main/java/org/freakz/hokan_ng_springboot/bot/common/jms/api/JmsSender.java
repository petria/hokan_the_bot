package org.freakz.hokan_ng_springboot.bot.common.jms.api;

import org.freakz.hokan_ng_springboot.bot.common.enums.HokanModule;
import org.freakz.hokan_ng_springboot.bot.common.jms.JmsMessage;

import javax.jms.Destination;
import javax.jms.ObjectMessage;

/**
 * Created by petria on 5.2.2015.
 * -
 */
public interface JmsSender {

    ObjectMessage sendAndGetReply(String destination, String key, Object object, boolean deliveryPersistent);

    void send(HokanModule hokanModule, String destination, String key, Object object, boolean deliveryPersistent);

    void sendJmsMessage(Destination destination, JmsMessage jmsMessage);

}
