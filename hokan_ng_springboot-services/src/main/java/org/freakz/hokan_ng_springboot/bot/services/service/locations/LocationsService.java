package org.freakz.hokan_ng_springboot.bot.services.service.locations;

import org.freakz.hokan_ng_springboot.bot.common.models.WorldCityData;

import java.util.List;
import java.util.Map;

public interface LocationsService {

    int fetchLocations();

    Map<String, WorldCityData> getCityDataMap();

    List<WorldCityData> findMatchingCities(String regexp);
}
