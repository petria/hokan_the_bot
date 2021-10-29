package org.freakz.hokan_ng_springboot.bot.services.service.distance;

import lombok.extern.slf4j.Slf4j;
import org.freakz.hokan_ng_springboot.bot.common.events.ServiceRequest;
import org.freakz.hokan_ng_springboot.bot.common.events.ServiceRequestType;
import org.freakz.hokan_ng_springboot.bot.common.events.ServiceResponse;
import org.freakz.hokan_ng_springboot.bot.services.service.annotation.ServiceMessageHandler;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Slf4j
public class DistanceService {


    @ServiceMessageHandler(ServiceRequestType = ServiceRequestType.MATKA_REQUEST)
    public void handleServiceRequest(ServiceRequest request, ServiceResponse response) {
        int foo = 0;
    }


    public String getDistance(String city1, String city2) {
        String url = String.format("https://www.vaelimatka.org/%s/%s", city1, city2);

        try {
            Document doc = Jsoup.connect(url).ignoreContentType(true).get();
            Elements element = doc.getElementsByClass("caption panel-footer");
            return element.text();

        } catch (IOException e) {
            log.error("Distance data fetch failed", e);
        }
        return null;
    }

}
