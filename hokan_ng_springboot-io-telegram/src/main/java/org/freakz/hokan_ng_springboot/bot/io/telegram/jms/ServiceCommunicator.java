package org.freakz.hokan_ng_springboot.bot.io.telegram.jms;

import org.freakz.hokan_ng_springboot.bot.common.events.IrcMessageEvent;
import org.freakz.hokan_ng_springboot.bot.common.events.ServiceRequestType;

/**
 * Created by Petri Airio on 27.8.2015.
 * -
 */
public interface ServiceCommunicator {

    void sendServiceRequest(IrcMessageEvent ircEvent, ServiceRequestType requestType);

}
