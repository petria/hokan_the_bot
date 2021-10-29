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
import java.util.Iterator;

/**
 * Created by airiope on 26.6.2017.
 */
@Component
public class AntellLunchPlaceHandler implements LunchRequestHandler {

    private static final Logger log = LoggerFactory.getLogger(AntellLunchPlaceHandler.class);

    private static String[] markers =
            {"Maanantai", "Tiistai", "Keskiviikko"};

    @Override
    @LunchPlaceHandler(LunchPlace = LunchPlace.LOUNAS_INFO_ANTELL)
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
        Elements rows = doc.getElementsByTag("tr");
        int markersIdx = 0;
        Iterator<Element> iterator = rows.iterator();
        boolean firstMarkerFound = false;
        String allText = "";
        while (iterator.hasNext()) {
            Element element = iterator.next();
            String text = element.text();
            allText += text;
        }
        int idx1 = allText.indexOf("Maanantai");
        int idx2 = allText.indexOf("Tiistai");
        String maMenu = allText.substring(idx1 + 9, idx2).trim();

        int idx3 = allText.indexOf("Keskiviikko");
        String tiMenu = allText.substring(idx2 + 7, idx3).trim();

        int idx4 = allText.indexOf("Torstai");
        String keMenu = allText.substring(idx3 + 11, idx4).trim();

        int idx5 = allText.indexOf("Perjantai");
        String toMenu = allText.substring(idx4 + 8, idx5).trim();

        int idx6 = allText.indexOf("M maidoton");
        String peMenu = allText.substring(idx5 + 10, idx6).trim();

        response.getMenu().put(LunchDay.MONDAY, new LunchMenu(maMenu));
        response.getMenu().put(LunchDay.TUESDAY, new LunchMenu(tiMenu));
        response.getMenu().put(LunchDay.WEDNESDAY, new LunchMenu(keMenu));
        response.getMenu().put(LunchDay.THURSDAY, new LunchMenu(toMenu));
        response.getMenu().put(LunchDay.FRIDAY, new LunchMenu(peMenu));

    }
}