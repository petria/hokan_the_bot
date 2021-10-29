package org.freakz.hokan_ng_springboot.bot.common.util;

import org.freakz.hokan_ng_springboot.bot.common.models.StartAndEndTime;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by Petri Airio on 21.8.2015.
 */
public class TimeUtil {

    public static StartAndEndTime getStartAndEndTimeForDay(LocalDateTime day) {
        LocalDateTime todayStart = day.toLocalDate().atStartOfDay();
        LocalDateTime tomorrowStart = day.plusDays(1).toLocalDate().atStartOfDay();
        return new StartAndEndTime(todayStart, tomorrowStart);
    }

    public static LocalDateTime parseDateTime(String string) {
        String[] parts = string.split("\\.");
        int dd = Integer.parseInt(parts[0]);
        int mm = Integer.parseInt(parts[1]);
        LocalDateTime parsed = LocalDateTime.now().withDayOfMonth(dd).withMonth(mm);
        return parsed;
    }

    public static Date localDateTimeToDate(LocalDateTime localDateTime) {
        ZonedDateTime zdt = ZonedDateTime.of(localDateTime, ZoneId.systemDefault());
        GregorianCalendar cal = GregorianCalendar.from(zdt);
        return cal.getTime();
    }
}
