package org.freakz.hokan_ng_springboot.bot.common.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Petri Airio on 12.8.2016.
 * -
 */
public class CityData implements Serializable {

    private List<String> resolvedCityNames = new ArrayList<>();

    public CityData() {
    }

    public CityData(List<String> resolvedCityNames) {
        this.resolvedCityNames = resolvedCityNames;
    }

    public List<String> getResolvedCityNames() {
        return resolvedCityNames;
    }

    public void setResolvedCityNames(List<String> resolvedCityNames) {
        this.resolvedCityNames = resolvedCityNames;
    }
}
