package org.freakz.hokan_ng_springboot.bot.common.models;


import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by Petri Airio on 6.10.2015.
 */
public class NimipaivaData implements Serializable {

    private LocalDateTime day;
    private List<String> names;

    public NimipaivaData() {
    }

    public NimipaivaData(LocalDateTime day, List<String> names) {
        this.day = day;
        this.names = names;
    }


    public LocalDateTime getDay() {
        return day;
    }

    public void setDay(LocalDateTime day) {
        this.day = day;
    }

    public List<String> getNames() {
        return names;
    }

    public void setNames(List<String> names) {
        this.names = names;
    }
}
