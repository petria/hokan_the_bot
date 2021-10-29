package org.freakz.hokan_ng_springboot.bot.common.enums;


import java.time.LocalDateTime;

/**
 * Created by Petri Airio on 28.1.2016.
 * -
 */
public enum LunchDay {

    MONDAY("Maanantai", "Monday"),
    TUESDAY("Tiistai", "Tuesday"),
    WEDNESDAY("Keskiviikko", "Wednesday"),
    THURSDAY("Torstai", "Thursday"),
    FRIDAY("Perjantai", "Friday"),
    SATURDAY("Lauantai", "Saturday"),
    SUNDAY("Sunnuntai", "Sunday");

    private final String dayFin;
    private final String dayEng;

    LunchDay(final String dayFin, String dayEng) {
        this.dayFin = dayFin;
        this.dayEng = dayEng;
    }

    public static LunchDay getFromDateTime(LocalDateTime day) {
        switch (day.getDayOfWeek()) {
            case MONDAY:
                return LunchDay.MONDAY;
            case TUESDAY:
                return LunchDay.TUESDAY;
            case WEDNESDAY:
                return LunchDay.WEDNESDAY;
            case THURSDAY:
                return LunchDay.THURSDAY;
            case FRIDAY:
                return LunchDay.FRIDAY;
        }
        return null;
    }

    public static LunchDay getFromWeekdayString(String weekday) {
        for (LunchDay lunchDay : values()) {
            if (weekday.toLowerCase().contains(lunchDay.dayFin.toLowerCase())) {
                return lunchDay;
            }
            if (weekday.toLowerCase().contains(lunchDay.dayEng.toLowerCase())) {
                return lunchDay;
            }
        }
        return null;
    }

    public String getDayFin() {
        return dayFin;
    }

    public String getDayEng() {
        return dayEng;
    }

}
