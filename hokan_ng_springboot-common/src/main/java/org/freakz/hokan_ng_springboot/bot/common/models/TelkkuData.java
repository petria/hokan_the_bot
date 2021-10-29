package org.freakz.hokan_ng_springboot.bot.common.models;

import java.util.Calendar;
import java.util.List;

/**
 * User: petria
 * Date: 11/26/13
 * Time: 12:45 PM
 *
 * @author Petri Airio <petri.j.airio@gmail.com>
 */
public class TelkkuData {

    private List<String> channelNames;
    private List<TelkkuProgram> programs;
    private Calendar lastUpdate;

    public TelkkuData(List<String> channelNames, List<TelkkuProgram> programs, Calendar lastUpdate) {
        this.channelNames = channelNames;
        this.programs = programs;
        this.lastUpdate = lastUpdate;
    }

    public List<String> getChannelNames() {
        return channelNames;
    }

    public void setChannelNames(List<String> channelNames) {
        this.channelNames = channelNames;
    }

    public List<TelkkuProgram> getPrograms() {
        return programs;
    }

    public void setPrograms(List<TelkkuProgram> programs) {
        this.programs = programs;
    }

    public Calendar getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Calendar lastUpdate) {
        this.lastUpdate = lastUpdate;
    }
}
