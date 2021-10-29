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
 * Created by Petri Airio on 2.3.2016.
 * -
 */
@Component
public class QulkuriLunchPlaceHandler implements LunchRequestHandler {

    private static final Logger log = LoggerFactory.getLogger(QulkuriLunchPlaceHandler.class);

    @Override
    @LunchPlaceHandler(LunchPlace = LunchPlace.LOUNAS_INFO_QULKURI)
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

        Elements rows = doc.getElementsByClass("row");
        Element element = rows.get(1);
        Elements byTag = element.getElementsByTag("p");
        String text1 = byTag.get(0).text();
        String text2 = byTag.get(1).text();

        LunchDay lunchDay = LunchDay.MONDAY;
        LunchMenu lunchMenu = new LunchMenu(String.format("%s / %s", text1, text2));
        response.getMenu().put(lunchDay, lunchMenu);

        element = rows.get(2);
        byTag = element.getElementsByTag("p");
        text1 = byTag.get(0).text();
        text2 = byTag.get(1).text();

        lunchDay = LunchDay.TUESDAY;
        lunchMenu = new LunchMenu(String.format("%s / %s", text1, text2));
        response.getMenu().put(lunchDay, lunchMenu);


        element = rows.get(3);
        byTag = element.getElementsByTag("p");
        text1 = byTag.get(0).text();
        text2 = byTag.get(1).text();

        lunchDay = LunchDay.WEDNESDAY;
        lunchMenu = new LunchMenu(String.format("%s / %s", text1, text2));
        response.getMenu().put(lunchDay, lunchMenu);


        element = rows.get(4);
        byTag = element.getElementsByTag("p");
        text1 = byTag.get(0).text();
        text2 = byTag.get(1).text();

        lunchDay = LunchDay.THURSDAY;
        lunchMenu = new LunchMenu(String.format("%s / %s", text1, text2));
        response.getMenu().put(lunchDay, lunchMenu);

        element = rows.get(5);
        byTag = element.getElementsByTag("p");
        text1 = byTag.get(0).text();
        text2 = byTag.get(1).text();

        lunchDay = LunchDay.FRIDAY;
        lunchMenu = new LunchMenu(String.format("%s / %s", text1, text2));
        response.getMenu().put(lunchDay, lunchMenu);

    }

}
