package org.freakz.hokan_ng_springboot.bot.common.models;

import org.freakz.hokan_ng_springboot.bot.common.util.StringStuff;

import java.io.Serializable;
import java.util.Date;

/**
 * User: petria
 * Date: 16-Jan-2009
 * Time: 08:37:05
 *
 * @author Petri Airio <petri.j.airio@gmail.com>
 */
public class TelkkuProgram implements Comparable, Serializable {

    private static int idCounter = 1;

    private int id;
    private Date startD;
    private Date endD;

    private String title;
    private String desc;

    private String channel;

    private boolean notifyDone;

    public TelkkuProgram(Date startD, Date endD, String title, String desc, String channel) {
//    _start = start;
        this.id = idCounter;
        idCounter++;
        this.startD = startD;
        this.endD = endD;
        this.title = title;
        this.desc = desc;
        this.channel = channel;
        this.notifyDone = false;
    }

    public TelkkuProgram() {
    }

    public static void resetIdCounter() {
        idCounter = 0;
    }

    /**
     * @param o Other TelkkuProgram object
     * @return compare result of this and other object
     */
    public int compareTo(Object o) {
        TelkkuProgram other = (TelkkuProgram) o;
        return this.startD.compareTo(other.getStartTimeD());
    }

    /**
     * @return when does this program start ?
     */
    public Date getStartTimeD() {
        return this.startD;
    }

    /**
     * @return when does this program end at?
     */
    public Date getEndTimeD() {
        return this.endD;
    }

    /**
     * @return the channel of this program
     */
    public String getChannel() {
        return this.channel;
    }

    /**
     * @return the description of this program, may be null!
     */
    public String getDescription() {
        return this.desc;
    }

    /**
     * @return this object's String presentation
     */
    public String toString() {
        String startTime = StringStuff.formatTime(this.startD, StringStuff.STRING_STUFF_DF_DDMMYYYYHHMM);
        String endTime = StringStuff.formatTime(this.endD, StringStuff.STRING_STUFF_DF_HHMM);
        String ret = startTime + "-" + endTime + " [" + this.channel + "]: " + this.title;
        return ret;
    }

    public String getProgram() {
        return this.title;
    }

    public boolean isNotifyDone() {
        return this.notifyDone;
    }

    public void setNotifyDone(boolean notify) {
        this.notifyDone = notify;
    }

    public int getId() {
        return this.id;
    }

}
