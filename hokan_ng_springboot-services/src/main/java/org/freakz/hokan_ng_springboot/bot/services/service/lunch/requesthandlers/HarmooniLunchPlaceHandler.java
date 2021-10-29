package org.freakz.hokan_ng_springboot.bot.services.service.lunch.requesthandlers;

import org.freakz.hokan_ng_springboot.bot.common.enums.LunchDay;
import org.freakz.hokan_ng_springboot.bot.common.enums.LunchPlace;
import org.freakz.hokan_ng_springboot.bot.common.models.LunchData;
import org.freakz.hokan_ng_springboot.bot.common.models.LunchMenu;
import org.freakz.hokan_ng_springboot.bot.common.util.StaticStrings;
import org.freakz.hokan_ng_springboot.bot.services.service.annotation.LunchPlaceHandler;
import org.freakz.hokan_ng_springboot.bot.services.service.lunch.LunchRequestHandler;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.time.LocalDateTime;

/**
 * Created by Petri Airio on 26.1.2016.
 * -
 */
@Component
public class HarmooniLunchPlaceHandler implements LunchRequestHandler {

    private static final Logger log = LoggerFactory.getLogger(HarmooniLunchPlaceHandler.class);

    @Override
    @LunchPlaceHandler(LunchPlace = LunchPlace.LOUNAS_INFO_HARMOONI)
    public void handleLunchPlace(LunchPlace lunchPlaceRequest, LunchData response, LocalDateTime day) {
//        response.setLunchPlace(lunchPlaceRequest);
        String url = lunchPlaceRequest.getUrl();
        Document doc;
        try {
            Authenticator.setDefault(new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication("foo", "bar".toCharArray());
                }
            });
            doc = Jsoup.connect(url).timeout(0).userAgent(StaticStrings.HTTP_USER_AGENT).get();
        } catch (IOException e) {
            log.error("Could not fetch lunch from {}", url, e);
            return;
        }
        Elements elements = doc.getElementsByClass("entry-content");
        Elements h3 = elements.select("h3");

        for (Element element : h3) {
            String text = element.text();
            LunchDay lunchDay = LunchDay.getFromWeekdayString(text);

//      log.debug("{}", text);
            boolean gotAll = false;
            Element test = element.nextElementSibling();
            String lunchForDay = "";
            while (!gotAll) {
                // TODO better method to avoid looping forever
                String food;
                if (test == null) {
                    break;
                } else {
                    food = test.text();
                }
                if (food.matches("Maanantai.*|Tiistai.*|Keskiviikko.*|Torstai.*|Perjantai.*")) {
                    break;
                } else {
                    if (lunchForDay.length() > 0) {
                        lunchForDay += ", ";
                    }
                    lunchForDay += food;
//          log.debug(" ->{}", food);
                }
                test = test.nextElementSibling();
            }
            LunchMenu lunchMenu = new LunchMenu(lunchForDay);
            response.getMenu().put(lunchDay, lunchMenu);

        }
        int foo = 0;

    }


}
