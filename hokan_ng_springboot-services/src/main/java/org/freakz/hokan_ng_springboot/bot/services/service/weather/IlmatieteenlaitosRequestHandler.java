package org.freakz.hokan_ng_springboot.bot.services.service.weather;

import org.freakz.hokan_ng_springboot.bot.common.events.ServiceRequest;
import org.freakz.hokan_ng_springboot.bot.common.events.ServiceRequestType;
import org.freakz.hokan_ng_springboot.bot.common.events.ServiceResponse;
import org.freakz.hokan_ng_springboot.bot.common.models.HourlyWeatherData;
import org.freakz.hokan_ng_springboot.bot.common.util.StaticStrings;
import org.freakz.hokan_ng_springboot.bot.services.service.annotation.ServiceMessageHandler;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Petri Airio on 11.8.2016.
 * -
 */
@Component
public class IlmatieteenlaitosRequestHandler {

    private static final Logger log = LoggerFactory.getLogger(IlmatieteenlaitosRequestHandler.class);

    @ServiceMessageHandler(ServiceRequestType = ServiceRequestType.ILMATIETEENLAITOS_HOURLY_REQUEST)
    public void handleHourlyRequest(ServiceRequest request, ServiceResponse response) {
        try {
            String place = (String) request.getParameters()[0];
            String url = String.format("https://www.ilmatieteenlaitos.fi/saa/%s", place);
            log.debug("Hourly forecast from: {}", url);
            Document doc = Jsoup.connect(url).userAgent(StaticStrings.HTTP_USER_AGENT).get();

//      Elements select = doc.select("tr[id=hour-row-0]");
//      String test = select.text();
            int row = 0;
            List<String> times = new ArrayList<>();
            List<String> datas = new ArrayList<>();
            List<String> temps = new ArrayList<>();

            while (true) {
                String match = String.format("tr[id=hour-row-%d]", row);
                row++;
                Elements select = doc.select(match);
                String text = select.text();
                if (text != null && text.length() > 0) {
                    String[] split = text.split(" ");
                    times.add(split[0].split(":")[0]);
                    datas.add(text);
                    temps.add(split[1]);
                }
                if (text.length() == 0) {
                    break;
                }
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
