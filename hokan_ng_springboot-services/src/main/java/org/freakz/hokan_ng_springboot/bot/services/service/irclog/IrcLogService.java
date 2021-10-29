package org.freakz.hokan_ng_springboot.bot.services.service.irclog;

import java.time.LocalDateTime;

public interface IrcLogService {

    void logChannelMessage(LocalDateTime localDateTime, String network, String channel, String message);

}
