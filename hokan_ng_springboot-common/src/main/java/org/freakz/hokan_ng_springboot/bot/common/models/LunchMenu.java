package org.freakz.hokan_ng_springboot.bot.common.models;

import java.io.Serializable;

/**
 * Created by Petri Airio on 28.1.2016.
 * -
 */
public class LunchMenu implements Serializable {

    private final String menu;

    public LunchMenu(String menu) {
        this.menu = menu;
    }

    public String getMenu() {
        return menu;
    }
}
