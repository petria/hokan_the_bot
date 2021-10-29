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
public class TaikuriRuokalistaLunchPlaceHandler implements LunchRequestHandler {

    private static final Logger log = LoggerFactory.getLogger(TaikuriRuokalistaLunchPlaceHandler.class);

    @Override
    @LunchPlaceHandler(LunchPlace = LunchPlace.LOUNAS_INFO_TAIKURI_RUOKALISTA)

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
        final Elements taikuri = doc.getElementsByAttributeValue("style", "text-align: center;");
        for (Element element : taikuri) {
            String text = element.text();
            if (text.contains("Maanantai")) {
                int space = text.indexOf(" ");
                space = text.indexOf(" ", space + 1);
                LunchMenu lunchMenu = new LunchMenu(text.substring(space).trim());
                response.getMenu().put(LunchDay.MONDAY, lunchMenu);
            }
            if (text.contains("Tiistai")) {
                int space = text.indexOf(" ");
                space = text.indexOf(" ", space + 1);
                LunchMenu lunchMenu = new LunchMenu(text.substring(space).trim());
                response.getMenu().put(LunchDay.TUESDAY, lunchMenu);
            }
            if (text.contains("Keskiviikko")) {
                int space = text.indexOf(" ");
                space = text.indexOf(" ", space + 1);
                LunchMenu lunchMenu = new LunchMenu(text.substring(space).trim());
                response.getMenu().put(LunchDay.WEDNESDAY, lunchMenu);
            }
            if (text.contains("Torstai")) {
                int space = text.indexOf(" ");
                space = text.indexOf(" ", space + 1);
                LunchMenu lunchMenu = new LunchMenu(text.substring(space).trim());
                response.getMenu().put(LunchDay.THURSDAY, lunchMenu);
            }
            if (text.contains("Perjantai")) {
                int space = text.indexOf(" ");
                space = text.indexOf(" ", space + 1);
                LunchMenu lunchMenu = new LunchMenu(text.substring(space).trim());
                response.getMenu().put(LunchDay.FRIDAY, lunchMenu);
            }

        }
    }

}
