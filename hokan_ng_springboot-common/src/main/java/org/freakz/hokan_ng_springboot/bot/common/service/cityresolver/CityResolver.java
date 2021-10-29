package org.freakz.hokan_ng_springboot.bot.common.service.cityresolver;

import org.freakz.hokan_ng_springboot.bot.common.models.CityData;

/**
 * Created by Petri Airio on 12.8.2016.
 * -
 */
public interface CityResolver {

    CityData resolveCityNames(String query);

    CityData resolveCityNames(String[] cities);
}
