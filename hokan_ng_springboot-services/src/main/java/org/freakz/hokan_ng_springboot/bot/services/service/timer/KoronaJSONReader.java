package org.freakz.hokan_ng_springboot.bot.services.service.timer;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.freakz.hokan_ng_springboot.bot.services.model.json.FinnishCoronaHospitalData;
import org.freakz.hokan_ng_springboot.bot.services.model.json.Hospitalised;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class KoronaJSONReader {


    private final ObjectMapper objectMapper;

    public Integer[] readKoronaJSON() {
        String url = "https://w3qa5ydb4l.execute-api.eu-west-1.amazonaws.com/prod/finnishCoronaHospitalData";
        try {
            Document doc = Jsoup.connect(url).ignoreContentType(true).get();
            String jsonString = doc.body().text();
            FinnishCoronaHospitalData json = objectMapper.readValue(jsonString, FinnishCoronaHospitalData.class);
            return mapJsonData(json.getHospitalised());
        } catch (IOException e) {
            log.error("Korona data fetch failed", e);
        }
        return null;
    }

    private Integer[] mapJsonData(List<Hospitalised> hospitalised) {

        Hospitalised h = null;
        long latestTime = 0;
        for (Hospitalised hh : hospitalised) {
            if (hh.getArea().equals("Finland") && hh.getDate().getTime() > latestTime) {
                h = hh;
                latestTime = hh.getDate().getTime();
            }
        }
        if (h != null) {
//            log.debug("latest: {}", h);
            return new Integer[]{h.getDead(), h.getInIcu(), h.getInWard(), h.getTotalHospitalised()};
        }
        return null;
    }

}
