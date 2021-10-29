package org.freakz.hokan_ng_springboot.bot.common.models;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Petri Airio (petri.j.airio@gmail.com) 22/11/2016 / 8.53
 */
public class AlkoSearchResults implements Serializable {

    private List<String> results;

    public AlkoSearchResults() {
    }

    public List<String> getResults() {
        return results;
    }

    public void setResults(List<String> results) {
        this.results = results;
    }

}
