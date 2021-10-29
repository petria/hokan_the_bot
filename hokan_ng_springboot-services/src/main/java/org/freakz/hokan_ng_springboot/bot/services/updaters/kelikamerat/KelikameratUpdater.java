package org.freakz.hokan_ng_springboot.bot.services.updaters.kelikamerat;

import org.freakz.hokan_ng_springboot.bot.common.models.KelikameratUrl;
import org.freakz.hokan_ng_springboot.bot.common.models.KelikameratWeatherData;
import org.freakz.hokan_ng_springboot.bot.common.util.StringStuff;
import org.freakz.hokan_ng_springboot.bot.services.updaters.Updater;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

/**
 * Created by Petri Airio on 22.6.2015.
 */
@Component
public class KelikameratUpdater extends Updater {

    private static final Logger log = LoggerFactory.getLogger(KelikameratUpdater.class);

    private static final String BASE_ULR = "https://www.kelikamerat.info";

    private static final String[] KELIKAMERAT_URLS =
            {
                    "https://www.kelikamerat.info/kelikamerat/Etel%C3%A4-Karjala",
                    "https://www.kelikamerat.info/kelikamerat/Etel%C3%A4-Pohjanmaa",
                    "https://www.kelikamerat.info/kelikamerat/Etel%C3%A4-Savo",
                    "https://www.kelikamerat.info/kelikamerat/Kainuu",
                    "https://www.kelikamerat.info/kelikamerat/Kanta-H%C3%A4me",
                    "https://www.kelikamerat.info/kelikamerat/Keski-Pohjanmaa",
                    "https://www.kelikamerat.info/kelikamerat/Keski-Suomi",
                    "https://www.kelikamerat.info/kelikamerat/Kymenlaakso",
                    "https://www.kelikamerat.info/kelikamerat/Lappi",
                    "https://www.kelikamerat.info/kelikamerat/P%C3%A4ij%C3%A4t-H%C3%A4me",
                    "https://www.kelikamerat.info/kelikamerat/Pirkanmaa",
                    "https://www.kelikamerat.info/kelikamerat/Pohjanmaa",
                    "https://www.kelikamerat.info/kelikamerat/Pohjois-Karjala",
                    "https://www.kelikamerat.info/kelikamerat/Pohjois-Pohjanmaa",
                    "https://www.kelikamerat.info/kelikamerat/Pohjois-Savo",
                    "https://www.kelikamerat.info/kelikamerat/Satakunta",
                    "https://www.kelikamerat.info/kelikamerat/Uusimaa",
                    "https://www.kelikamerat.info/kelikamerat/Varsinais-Suomi"
            };

    private List<KelikameratUrl> stationUrls;

    private List<KelikameratWeatherData> weatherDataList;

    public void updateStations() throws IOException {
        List<KelikameratUrl> stations = new ArrayList<>();
        for (String url : KELIKAMERAT_URLS) {
            Document doc = Jsoup.connect(url).get();
            Elements elements = doc.getElementsByClass("road-camera");
            this.dataFetched += doc.html().length();
            this.itemsFetched++;
            for (int xx = 0; xx < elements.size(); xx++) {
                Element div = elements.get(xx);
                Element href = div.child(0);
                String hrefUrl = BASE_ULR + href.attributes().get("href");
                KelikameratUrl kelikameratUrl = new KelikameratUrl(url, hrefUrl);
                stations.add(kelikameratUrl);
            }
        }
        this.stationUrls = stations;
    }

    private Float parseFloat(String str) {
        String f = str.split(" ")[0];
        if (f != null) {
            //&& f.length() > 0 && !f.equals("-"))
            if (f.equals("-")) {
                return null;
            } else {
                return Float.parseFloat(f);
            }
        } else {
            return null;
        }
    }

    public KelikameratWeatherData updateKelikameratWeatherData(KelikameratUrl url) {
        Document doc;
        try {
            doc = Jsoup.connect(url.getStationUrl()).get();
            this.dataFetched += doc.html().length();
            this.itemsFetched++;
        } catch (IOException e) {
            //            log.error("Can't update data: {}", url.getStationUrl());
            return null;
        }
        String titleText = doc.getElementsByTag("title").get(0).text();

        titleText = titleText.replaceFirst("Kelikamerat - ", "").replaceFirst("\\| Kelikamerat", "").trim();

        Elements elements = doc.getElementsByClass("weather-details");
        Element div = elements.get(0);
        Element table = div.child(0);
        Element tbody = table.child(0);

        KelikameratWeatherData data = new KelikameratWeatherData();
        data.setPlace(titleText);
//        log.debug("place: {}", titleText);

        data.setUrl(url);

        int idx = url.getStationUrl().lastIndexOf("/");
        data.setPlaceFromUrl(StringStuff.htmlEntitiesToText(url.getStationUrl().substring(idx + 1)));

        String air = tbody.child(0).child(1).text();
        Float airFloat = parseFloat(air);
        if (airFloat == null) {
            return null;
        }
        data.setAir(airFloat);

        String road = tbody.child(1).child(1).text();
        data.setRoad(parseFloat(road));

        String ground = tbody.child(2).child(1).text();
        data.setGround(parseFloat(ground));
        //      log.debug("air: {} - road: {} - ground: {}", air);

        String humidity = tbody.child(3).child(1).text();
        data.setHumidity(parseFloat(humidity));

        String dewPoint = tbody.child(4).child(1).text();
        data.setDewPoint(parseFloat(dewPoint));

        Elements elements2 = doc.getElementsByClass("date-time");
        if (elements2.size() > 0) {
            String timestamp = elements2.get(0).text().substring(12);

            String pattern1 = "dd.MM.yyyy HH:mm:ss";
            String pattern2 = "dd.MM.yyyy H:mm:ss";
            //            DateTime dateTime = DateTime.parse(timestamp, DateTimeFormat.forPattern(pattern));

            LocalDateTime localDateTime;

            try {
                localDateTime = LocalDateTime.parse(timestamp, DateTimeFormatter.ofPattern(pattern1));
            } catch (DateTimeParseException exception) {
                localDateTime = LocalDateTime.parse(timestamp, DateTimeFormatter.ofPattern(pattern2));
            }

            data.setTime(localDateTime);
        }

        return data;
    }

    public List<KelikameratUrl> getStationUrls() {
        return stationUrls;
    }

    @Override
    public void doUpdateData() throws Exception {
        if (this.stationUrls == null) {
            updateStations();
        }
        List<KelikameratWeatherData> weatherDataList = new ArrayList<>();
        int success = 0;
        int failed = 0;
        for (KelikameratUrl url : this.stationUrls) {
            //            log.debug("Handle url: {}", url);
            KelikameratWeatherData data = updateKelikameratWeatherData(url);
            if (data != null) {
                weatherDataList.add(data);
                success++;
            } else {
                failed++;
            }
            //      log.debug("{}", String.format("%s: %1.2f °C", data.getPlaceFromUrl(), data.getAir()));
        }

        log.debug("Update done, success: {} / failed: {}", success, failed);
        Collections.sort(weatherDataList);
        this.itemCount = weatherDataList.size();
        this.weatherDataList = weatherDataList;
    }

    @Override
    public Object doGetData(Object... args) {
        if (this.weatherDataList != null) {
            //      return sortData(this.weatherDataList, true);
            return this.weatherDataList;
        }
        return null;
    }

    @Override
    public Calendar calculateNextUpdate() {
        Calendar cal = new GregorianCalendar();
        cal.add(Calendar.MINUTE, 30);
        return cal;
    }

    @SuppressWarnings("unchecked")
    public List<KelikameratWeatherData> sortData(List<KelikameratWeatherData> v, boolean reverse) {

        KelikameratWeatherComparator comparator = new KelikameratWeatherComparator(reverse);

        Collections.sort(v, comparator);

        KelikameratWeatherData wd = null;
        for (int xx = 0; xx < v.size(); xx++) {
            wd = v.get(xx);
            wd.setPos(xx + 1);
        }
        if (wd != null) {
            wd.setCount(v.size());
        }
        return v;
    }

    public static class KelikameratWeatherComparator implements Comparator {

        private boolean reverse;

        public KelikameratWeatherComparator(boolean reverse) {
            this.reverse = reverse;
        }

        public int compare(Object o1, Object o2) {
            KelikameratWeatherData w1 = (KelikameratWeatherData) o1;
            KelikameratWeatherData w2 = (KelikameratWeatherData) o2;
            int comp = w1.compareTo(w2);
            if (reverse) {
                comp = comp * -1;
            }
            return comp;
        }
    }
}
