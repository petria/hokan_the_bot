package org.freakz.hokan_ng_springboot.bot.common.events;

import java.time.LocalDateTime;

/**
 * User: petria
 * Date: 11/15/13
 * Time: 12:36 PM
 *
 * @author Petri Airio <petri.j.airio@gmail.com>
 */
public class IrcEventFactory {

    public static IrcEvent createIrcEvent(String botNick, String network, String channel, String sender, String login, String hostname) {
        IrcEvent ircEvent = new IrcEvent(botNick, network, channel, sender, login, hostname);
        ircEvent.setTimeStamp(LocalDateTime.now());
        return ircEvent;
    }

    public static IrcEvent createIrcMessageEvent(String botNick, String network, String channel, String sender, String login, String hostname, String message) {
        IrcMessageEvent ircMessageEvent = new IrcMessageEvent(botNick, network, channel, sender, login, hostname, message);
        ircMessageEvent.setTimeStamp(LocalDateTime.now());
        return ircMessageEvent;
    }


}
