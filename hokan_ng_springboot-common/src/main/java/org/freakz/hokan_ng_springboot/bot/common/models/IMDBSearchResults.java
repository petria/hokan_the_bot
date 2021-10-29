package org.freakz.hokan_ng_springboot.bot.common.models;


import java.io.Serializable;
import java.util.Map;

/**
 * Created by Petri Airio on 18.11.2015.
 * -
 */
public class IMDBSearchResults implements Serializable {

    private Map<String, Object> rawResults;

    public IMDBSearchResults() {
    }

    public Map<String, Object> getRawResults() {
        return rawResults;
    }

    public void setRawResults(Map<String, Object> rawResults) {
        this.rawResults = rawResults;
    }


}
