package org.freakz.hokan_ng_springboot.bot.common.service.cityresolver;

import lombok.extern.slf4j.Slf4j;
import org.freakz.hokan_ng_springboot.bot.common.models.CityData;
import org.freakz.hokan_ng_springboot.bot.common.util.FileUtil;
import org.freakz.hokan_ng_springboot.bot.common.util.StringStuff;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Petri Airio on 12.8.2016.
 * -
 */
@Service
@Slf4j
public class CityResolverImpl implements CityResolver {

    private String[] cityNames = null;

    @Autowired
    private FileUtil fileUtil;


    @Override
    public CityData resolveCityNames(String pattern) {
        return resolveCityNames(new String[]{pattern});
    }

    @Override
    public CityData resolveCityNames(String[] queries) {
        CityData cityData = new CityData();
        if (cityNames == null) {
            log.info("Reading city names file");
            StringBuilder sb = new StringBuilder();
            File tmpFile;
            try {
                tmpFile = File.createTempFile("kuntalista", "");
                fileUtil.copyResourceToFile("/kuntalista.txt", tmpFile, sb);
                cityNames = sb.toString().split("\n");
            } catch (IOException e) {
                log.error("Failed to get city stations", e);
                return cityData;
            }
        }
        List<String> matches = new ArrayList<>();

        for (String name : cityNames) {
            for (String query : queries) {
                if (StringStuff.match(name, ".*" + query + ".*", true)) {
                    matches.add(name);
                }
            }
        }
        cityData.setResolvedCityNames(matches);
        return cityData;
    }
}
