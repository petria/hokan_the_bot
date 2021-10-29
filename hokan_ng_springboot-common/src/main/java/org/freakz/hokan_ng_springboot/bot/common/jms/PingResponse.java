package org.freakz.hokan_ng_springboot.bot.common.jms;

import org.freakz.hokan_ng_springboot.bot.common.util.Uptime;

import java.io.Serializable;

/**
 * Created by Petri Airio on 9.4.2015.
 * -
 */
public class PingResponse implements Serializable {

    private String reply = "<pong>";
    private Uptime uptime;
    private String hostinfo;

    public PingResponse() {
    }

    public String getReply() {
        return reply;
    }

    public Uptime getUptime() {
        return uptime;
    }

    public void setUptime(Uptime uptime) {
        this.uptime = uptime;
    }

    public String getHostinfo() {
        return hostinfo;
    }

    public void setHostinfo(String hostinfo) {
        this.hostinfo = hostinfo;
    }
}
