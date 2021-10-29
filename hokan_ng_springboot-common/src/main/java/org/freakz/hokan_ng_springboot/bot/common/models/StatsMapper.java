package org.freakz.hokan_ng_springboot.bot.common.models;

import java.io.Serializable;
import java.util.*;

/**
 * Created by Petri Airio on 21.8.2015.
 */
public class StatsMapper implements Serializable {


    private String error;
    private Map<String, StatsData> statsDataMap = new HashMap<>();

    public StatsMapper() {
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public StatsData getStatsDataForNick(String nick) {
        String nickLower = nick.toLowerCase();
        StatsData statsData = statsDataMap.get(nickLower);
        if (statsData == null) {
            statsData = new StatsData(nickLower);
            statsDataMap.put(nickLower, statsData);
        }
        return statsData;
    }

    public boolean hasError() {
        return error != null;
    }

    public List<StatsData> getStatsData() {
        List<StatsData> dataList = new ArrayList<>(statsDataMap.values());
        Comparator<? super StatsData> comparator = (o1, o2) -> {
            int w1 = o1.getWords();
            int w2 = o2.getWords();
            if (w1 > w2) {
                return -1;
            }
            if (w1 < w2) {
                return +1;
            }
            return 0;
        };
        Collections.sort(dataList, comparator);
        return dataList;
    }
}
