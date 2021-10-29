package org.freakz.hokan_ng_springboot.bot.services.service.weather;

import org.freakz.hokan_ng_springboot.bot.common.events.ServiceRequest;
import org.freakz.hokan_ng_springboot.bot.common.events.ServiceRequestType;
import org.freakz.hokan_ng_springboot.bot.common.events.ServiceResponse;
import org.freakz.hokan_ng_springboot.bot.common.models.HourlyWeatherData;
import org.freakz.hokan_ng_springboot.bot.common.util.StaticStrings;
import org.freakz.hokan_ng_springboot.bot.services.service.annotation.ServiceMessageHandler;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Petri Airio on 5.6.2020.
 * -
 */
@Component
public class ForecaWeatherRequestHandler {

    private static final Logger log = LoggerFactory.getLogger(ForecaWeatherRequestHandler.class);

    @ServiceMessageHandler(ServiceRequestType = ServiceRequestType.FORECA_WEATHER_HOURLY_REQUEST)
    public void handleHourlyRequest(ServiceRequest request, ServiceResponse response) {
        try {
            int year = LocalDate.now().getYear();
            int day = LocalDate.now().getDayOfMonth();
            int month = LocalDate.now().getMonth().getValue();
            String date = String.format("%d%02d%02d", year, month, day);
            String place = (String) request.getParameters()[0];
            String url = String.format("https://www.foreca.fi/Finland/%s/details", place);
            log.debug("Hourly forecast from: {}", url);
            Document doc = Jsoup.connect(url).userAgent(StaticStrings.HTTP_USER_AGENT).get();
            Elements ss = doc.getElementsByTag("script");
            for (Element script : ss) {
                String text = script.data();
                if (text.contains("daily_data")) {
                    String json = text.replaceFirst("\\(function\\(\\) \\{ var daily_data =", "");
                    int foo = 0;
                }
            }
            Elements c0 = doc.getElementsByClass("c0");
            Elements c3 = doc.getElementsByClass("c3");
            Elements c4 = doc.getElementsByClass("c4");
            List<String> times = new ArrayList<>();
            List<String> datas = new ArrayList<>();
            List<String> temps = new ArrayList<>();
            for (int idx = 0; idx < c0.size(); idx++) {
                Element time = c0.get(idx);
                String t1 = time.text().split(":")[0];
                times.add(t1);

                Element data = c3.get(idx);
                String t2 = data.text();
                datas.add(t2);

                Element temp = c4.get(idx);
                String t3 = temp.text();
                temps.add(t3);

            }

            HourlyWeatherData hourly = new HourlyWeatherData();
            hourly.setTimes(times.toArray(new String[]{}));
            hourly.setTemperatures(temps.toArray(new String[]{}));
            hourly.setSymbols(datas.toArray(new String[]{}));

            response.setResponseData(request.getType().getResponseDataKey(), hourly);

        } catch (Exception e) {
            //
        }

    }

}
