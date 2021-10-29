package org.freakz.hokan_ng_springboot.bot.io.jms;

import org.freakz.hokan_ng_springboot.bot.common.events.IrcMessageEvent;

/**
 * Created by Petri Airio on 31.8.2017.
 */
public interface TelegramCommunicator {

    void sendIrcMessageEventToTelegram(IrcMessageEvent ircMessageEvent);

    void sendMessageToTelegram(String message, String telegramChatId);

}
