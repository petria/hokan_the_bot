package org.freakz.hokan_ng_springboot.bot.common.models;

import org.freakz.hokan_ng_springboot.bot.common.enums.LunchDay;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Petri Airio on 21.1.2016.
 * -
 */
public class LunchData implements Serializable {

    private Map<LunchDay, LunchMenu> menu = new HashMap<>();

    public LunchData() {
    }

    public Map<LunchDay, LunchMenu> getMenu() {
        return menu;
    }

    @Override
    public String toString() {
        return "menu :: " + menu;
    }

}
