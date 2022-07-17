package org.freakz.hokan_ng_springboot.bot.services.service.timer;

import lombok.extern.slf4j.Slf4j;
import org.freakz.hokan_ng_springboot.bot.common.events.ServiceRequest;
import org.freakz.hokan_ng_springboot.bot.common.events.ServiceRequestType;
import org.freakz.hokan_ng_springboot.bot.common.events.ServiceResponse;
import org.freakz.hokan_ng_springboot.bot.common.models.NatoRatifyStats;
import org.freakz.hokan_ng_springboot.bot.common.util.StaticStrings;
import org.freakz.hokan_ng_springboot.bot.services.service.annotation.ServiceMessageHandler;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
@Slf4j
public class NatoRatificationsService {


    public Elements fetchDataTable() {
        String url = "https://www.viastar.fi/laskuri/NATO-laskuri/en/";
        try {
            Document doc = Jsoup.connect(url).userAgent(StaticStrings.HTTP_USER_AGENT).get();
            Elements tableRows = doc.select("table.viastar").select("tr");
            return tableRows;
        } catch (IOException e) {
            log.error("OTAN data fetch failed", e);
        }

        return null;
    }

    public NatoRatifyStats fetchNatoData() {
        Elements elements = fetchDataTable();
        if (elements != null) {
            List<String> strings = elements.eachText();
            if (strings.size() == 34) {
                NatoRatifyStats stats = new NatoRatifyStats();
                for (int i = 1; i < 31; i++) {
                    String str = strings.get(i);
                    if (str.startsWith("X ")) {
                        stats.getNotRatified().add(str);
                    } else {
                        stats.getRatified().add(str);
                    }
                }


                stats.setSummary(strings.get(32));
                stats.setUpdated(strings.get(33));
                return stats;
            }
        }
        return null;
    }


    @ServiceMessageHandler(ServiceRequestType = ServiceRequestType.NATO_REQUEST)
    public void handleNatoNotifyCmdRequest(ServiceRequest request, ServiceResponse response) {
        NatoRatifyStats natoRatifyStats = fetchNatoData();
        response.setResponseData(request.getType().getResponseDataKey(), natoRatifyStats);
    }

}
