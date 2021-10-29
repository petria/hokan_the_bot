package org.freakz.hokan_ng_springboot.bot.common.models;

import java.io.Serializable;

/**
 * Created by Petri Airio on 2.9.2015.
 */
public class GoogleCurrency implements Serializable {

    private String shortName;
    private String longName;

    public GoogleCurrency() {
    }

    public GoogleCurrency(String shortName, String longName) {
        this.shortName = shortName;
        this.longName = longName;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getLongName() {
        return longName;
    }

    public void setLongName(String longName) {
        this.longName = longName;
    }
}
