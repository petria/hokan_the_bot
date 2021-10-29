package org.freakz.hokan_ng_springboot.bot.services.service.irclog;

import java.time.LocalDateTime;
import java.util.Iterator;

public interface LocalDateTimeProvider {

    LocalDateTime getLocalDateTime();

    void setIterator(Iterator<LocalDateTime> iterator);

}
