package org.freakz.hokan_ng_springboot.bot.services.service.locations;

import org.freakz.hokan_ng_springboot.bot.common.models.WorldCityData;
import org.freakz.hokan_ng_springboot.bot.common.util.StringStuff;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

@Service
public class LocationsServiceImpl implements LocationsService {

    private static final Logger log = LoggerFactory.getLogger(LocationsServiceImpl.class);

    private static final String LOCATIONS_FILE_URL = "http://download.maxmind.com/download/worldcities/worldcitiespop.txt.gz";

    private final Map<String, WorldCityData> cityDataMap = new HashMap<>();

    @Override
    public int fetchLocations() {
        int count = -1;
        try {
            count = downloadLocationsFile(LOCATIONS_FILE_URL);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }

    //    @PostConstruct
    public void init() {
        Thread t = new Thread(() -> {
            final int count = fetchLocations();
            log.debug("City count: {}", count);

        });
        t.start();
    }

    private int downloadLocationsFile(String remoteUrl) throws Exception {
        URL url = new URL(remoteUrl);
        //open the connection to the above URL.
        URLConnection urlConnection = url.openConnection();
        GZIPInputStream gzipInputStream = new GZIPInputStream(urlConnection.getInputStream());
        Reader decoder = new InputStreamReader(gzipInputStream, "ISO-8859-1");
        BufferedReader buffered = new BufferedReader(decoder);
        int linesRead = 0;
        this.cityDataMap.clear();
        boolean first = true;
        do {
            String line = buffered.readLine();
            if (line != null) {
                linesRead++;
                if (first) {
                    first = false;
                    continue; // skip first header row
                }
                String[] split = line.split(",");
                if (split.length == 7) {
                    WorldCityData wcd = new WorldCityData();
                    wcd.setCountryCode(split[0]);
                    wcd.setCity(split[1]);
                    wcd.setAccentCity(split[2]);
                    wcd.setRegion(split[3]);
                    // skip split[6] Population field
                    wcd.setLatitude(Double.parseDouble(split[5]));
                    wcd.setLongitude(Double.parseDouble(split[6]));
                    this.cityDataMap.put(wcd.toString(), wcd);
                } else {
                    int foo = 0;
                }
            } else {
                break;
            }
        } while (true);
        return linesRead;
    }

    @Override
    public Map<String, WorldCityData> getCityDataMap() {
        return cityDataMap;
    }

    @Override
    public List<WorldCityData> findMatchingCities(String regexp) {
        List<WorldCityData> list = new ArrayList<>();
        for (WorldCityData worldCityData : cityDataMap.values()) {
            if (StringStuff.match(worldCityData.toString(), ".*" + regexp + ".*", true)) {
                list.add(worldCityData);
            }
        }
        return list;
    }

}
