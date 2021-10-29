package org.freakz.hokan_ng_springboot.bot.common.enums;

import org.freakz.hokan_ng_springboot.bot.common.util.StringStuff;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Petri Airio on 21.1.2016.
 * -
 */
public enum LunchPlace {

    LOUNAS_INFO_ANTELL("Jyväskylä/Antell ylistönmäki", "http://www.antell.fi/lounaslistat/lounaslista.html?owner=74"),
    LOUNAS_INFO_HARMOONI("Jyväskylä/Harmooni", "https://www.harmooni.fi/ravintola/lounas/"),
    LOUNAS_INFO_HERKKUPISTE("Turku/Herkkupiste", "http://www.herkkupiste.fi/lounaslista.html"),
    LOUNAS_INFO_HKI_TERMINAALI2("Helsinki/Terminaali2", "http://www.sspfinland.fi/fi/helsinki-vantaan-lentoaseman-ravintolat-kahvilat-ja-lounget/terminaali-2/ravintolat/cesars-pizza-and-food-court/"),
    LOUNAS_INFO_HOX("Jyväskylä/Ravintola HOX", "http://ravintolahox.fi/lounas/"),
    LOUNAS_INFO_SHALIMAR("Jyväskylä/Shalimar", "http://www.ravintolashalimar.fi/index.php?page=lounasjkl"),
    LOUNAS_INFO_TAIKURI_RUOKALISTA("Jyväskylä/Taikuri Street Food", "http://www.ravintolataikuri.fi/taikurin-street-food-buffet/"),
    LOUNAS_INFO_QULKURI("Jyväskylä/Qulkuri", "http://www.qulkuri.fi/#lutakko-lounaslista");

    private final String url;
    private final String name;

    LunchPlace(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public static LunchPlace getLunchPlace(String argLunchPlace) {
        for (LunchPlace lunchPlace : values()) {
            if (lunchPlace.getName().toLowerCase().contains(argLunchPlace.toLowerCase())) {
                return lunchPlace;
            }
        }
        return null;
    }

    public static Set<LunchPlace> getMatchingLunchPlaces(String argLunchPlace) {
        Set<LunchPlace> matching = new HashSet<>();
        for (LunchPlace lunchPlace : values()) {
            if (StringStuff.match(lunchPlace.getName(), ".*" + argLunchPlace + ".*", true)) {
                matching.add(lunchPlace);
            }
        }
        return matching;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

}
