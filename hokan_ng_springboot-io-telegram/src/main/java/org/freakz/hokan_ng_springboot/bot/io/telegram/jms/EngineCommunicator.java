package org.freakz.hokan_ng_springboot.bot.io.telegram.jms;


import org.freakz.hokan_ng_springboot.bot.common.events.IrcMessageEvent;
import org.freakz.hokan_ng_springboot.bot.common.jpa.entity.UserChannel;

/**
 * Created by Petri Airio on 9.4.2015.
 * -
 */
public interface EngineCommunicator {

    String sendToEngine(IrcMessageEvent event, UserChannel userChannel);

    void sendToIrcChannel(IrcMessageEvent event, int chanId);

}
