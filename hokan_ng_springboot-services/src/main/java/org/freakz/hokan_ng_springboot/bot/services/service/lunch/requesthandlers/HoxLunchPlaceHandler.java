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

@Component
public class HoxLunchPlaceHandler implements LunchRequestHandler {

    private static final Logger log = LoggerFactory.getLogger(HoxLunchPlaceHandler.class);

    @Override
    @LunchPlaceHandler(LunchPlace = LunchPlace.LOUNAS_INFO_HOX)
    public void handleLunchPlace(LunchPlace lunchPlaceRequest, LunchData response, LocalDateTime day) {
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
        Elements rows = doc.getElementsByClass("keskitetty");
        for (Element element : rows) {
            String text = element.text();
            if (text.contains("Maanantai")) {
                int idx1 = text.indexOf("Maanantai");
                int idx2 = text.indexOf("Tiistai");
                int idx3 = text.indexOf("Keskiviikko");
                int idx4 = text.indexOf("Torstai");
                int idx5 = text.indexOf("Perjantai");
                String lunch1 = text.substring(idx1, idx2).replace("Maanantai ", "").trim();
                String lunch2 = text.substring(idx2, idx3).replace("Tiistai ", "").trim();
                String lunch3 = text.substring(idx3, idx4).replace("Keskiviikko ", "").trim();
                String lunch4 = text.substring(idx4, idx5).replace("Torstai ", "").trim();
                String lunch5 = text.substring(idx5).replace("Perjantai ", "").trim();

                response.getMenu().put(LunchDay.MONDAY, new LunchMenu(lunch1));
                response.getMenu().put(LunchDay.TUESDAY, new LunchMenu(lunch2));
                response.getMenu().put(LunchDay.WEDNESDAY, new LunchMenu(lunch3));
                response.getMenu().put(LunchDay.THURSDAY, new LunchMenu(lunch4));
                response.getMenu().put(LunchDay.FRIDAY, new LunchMenu(lunch5));
            }
        }

    }

}
