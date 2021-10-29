package org.freakz.hokan_ng_springboot.bot.common.models;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Petri Airio on 20.5.2015.
 * -
 */
public class TvNowData implements Serializable {

    final private String[] channels;
    final private Map<String, TelkkuProgram> currentPrograms;
    final private Map<String, TelkkuProgram> nextPrograms;

    public TvNowData(String[] channels, Map<String, TelkkuProgram> currentPrograms, Map<String, TelkkuProgram> nextPrograms) {
        this.channels = channels;
        this.currentPrograms = currentPrograms;
        this.nextPrograms = nextPrograms;
    }

    public TvNowData() {
        this.channels = new String[0];
        this.currentPrograms = new HashMap<>();
        this.nextPrograms = new HashMap<>();
    }

    public String[] getChannels() {
        return this.channels;
    }

    public TelkkuProgram getCurrentProgram(String channel) {
        return currentPrograms.get(channel);
    }

    public TelkkuProgram getNextProgram(String channel) {
        return nextPrograms.get(channel);
    }


}
