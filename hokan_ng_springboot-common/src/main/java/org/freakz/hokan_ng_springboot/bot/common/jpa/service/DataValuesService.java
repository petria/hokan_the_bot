package org.freakz.hokan_ng_springboot.bot.common.jpa.service;

import org.freakz.hokan_ng_springboot.bot.common.models.DataValueStatsModel;
import org.freakz.hokan_ng_springboot.bot.common.models.DataValuesModel;

import java.util.List;

public interface DataValuesService {

    List<DataValuesModel> getDataValues(String channel, String network, String key);

    List<DataValuesModel> getDataValuesAsc(String channel, String network, String key);

    List<DataValuesModel> getDataValuesDesc(String channel, String network, String key);

    String getValue(String nick, String channel, String network, String key);

    void setValue(String nick, String channel, String network, String key, String value);

    DataValueStatsModel getValueStats(String nick, String channel, String network, String key);
}
