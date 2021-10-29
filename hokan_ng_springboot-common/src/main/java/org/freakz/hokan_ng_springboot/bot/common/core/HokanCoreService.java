package org.freakz.hokan_ng_springboot.bot.common.core;

/**
 * Created by Petri Airio (petri.j.airio@gmail.com) on 15.4.2015.
 */
public interface HokanCoreService {

    void handleSendMessage(String channel, String message, boolean doSr, String prefix, String postfix);

}
