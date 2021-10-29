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
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.time.LocalDateTime;

/**
 * Created by Petri Airio on 12.2.2016.
 * -
 */
@Component
public class HerkkupisteLunchPlaceHandler implements LunchRequestHandler {

    private static final Logger log = LoggerFactory.getLogger(HerkkupisteLunchPlaceHandler.class);

    @Override
    @LunchPlaceHandler(LunchPlace = LunchPlace.LOUNAS_INFO_HERKKUPISTE)
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
        Elements elements = doc.getElementsByClass("lounas");
        Elements tds = elements.select("td");
        for (int idx = 0; idx < 10; idx += 2) {
            String lunchForDay = tds.get(idx + 1).text();
            LunchDay lunchDay = LunchDay.getFromWeekdayString(tds.get(idx).text());
            LunchMenu lunchMenu = new LunchMenu(lunchForDay);
            response.getMenu().put(lunchDay, lunchMenu);
        }
    }
}
