package org.freakz.hokan_ng_springboot.bot.common.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class FindCityResults implements Serializable {

    private List<WorldCityData> worldCityDataList = new ArrayList<>();

    public List<WorldCityData> getWorldCityDataList() {
        return worldCityDataList;
    }

    public void setWorldCityDataList(List<WorldCityData> list) {
        this.worldCityDataList = list;
    }

}
