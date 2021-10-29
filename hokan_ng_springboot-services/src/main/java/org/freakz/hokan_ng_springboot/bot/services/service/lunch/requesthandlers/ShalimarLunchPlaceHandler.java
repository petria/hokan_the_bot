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

@Component
public class ShalimarLunchPlaceHandler implements LunchRequestHandler {

    private static final Logger log = LoggerFactory.getLogger(ShalimarLunchPlaceHandler.class);

    @Override
    @LunchPlaceHandler(LunchPlace = LunchPlace.LOUNAS_INFO_SHALIMAR)
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

            final Elements table = doc.select("table");
            final Elements td = table.select("td");

            StringBuilder menu = new StringBuilder();
            for (Iterator<Element> it = td.iterator(); it.hasNext(); ) {
                Element element = it.next();
                String text = element.text();
                if (text.length() == 0) {
                    text = " | ";
                }
                if (text.matches("(Maanantai.*|Tiistai.*|Keskiviikko.*|Torstai.*|Perjantai.*)")) {
                    if (menu.length() > 0) {
                        menu.append("\n");
                    }
                    menu.append(text);
                    menu.append(" ");
                } else if (text.length() > 0) {
                    if (text.contains("€")) {
                        continue;
                    }
                    menu.append(text);
                    menu.append(" ");
                }
                //                System.out.printf("%s\n", );
            }
            String[] split = menu.toString().split("\n");

            response.getMenu().put(LunchDay.MONDAY, new LunchMenu(split[1]));
            response.getMenu().put(LunchDay.TUESDAY, new LunchMenu(split[2]));
            response.getMenu().put(LunchDay.WEDNESDAY, new LunchMenu(split[3]));
            response.getMenu().put(LunchDay.THURSDAY, new LunchMenu(split[4]));
            response.getMenu().put(LunchDay.FRIDAY, new LunchMenu(split[5]));

            int foo = 0;

        } catch (IOException e) {
            log.error("Could not fetch lunch from {}", url, e);

            return;
        }

    }

}
