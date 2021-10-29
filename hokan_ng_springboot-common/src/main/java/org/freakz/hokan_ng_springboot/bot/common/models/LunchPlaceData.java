package org.freakz.hokan_ng_springboot.bot.common.models;

import org.freakz.hokan_ng_springboot.bot.common.enums.LunchPlace;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Petri Airio on 28.6.2017.
 */
public class LunchPlaceData implements Serializable {

    private Map<LunchPlace, LunchData> lunchDataMap = new HashMap<>();

    public Map<LunchPlace, LunchData> getLunchDataMap() {
        return lunchDataMap;
    }

    public void setLunchDataMap(Map<LunchPlace, LunchData> lunchDataMap) {
        this.lunchDataMap = lunchDataMap;
    }
}
