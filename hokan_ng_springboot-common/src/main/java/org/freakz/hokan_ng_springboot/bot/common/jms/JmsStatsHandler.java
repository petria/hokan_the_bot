package org.freakz.hokan_ng_springboot.bot.common.jms;

/**
 * Created by Petri Airio (petri.j.airio@gmail.com) 14/10/2016 / 12.09
 */
public interface JmsStatsHandler {


    void messageSent(String target);

    void messageReceived(String target);

}
