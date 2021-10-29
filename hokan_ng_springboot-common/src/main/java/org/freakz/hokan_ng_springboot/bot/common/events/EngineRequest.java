package org.freakz.hokan_ng_springboot.bot.common.events;

import java.io.Serializable;

/**
 * User: petria
 * Date: 11/6/13
 * Time: 10:42 AM
 *
 * @author Petri Airio <petri.j.airio@gmail.com>
 */
public class EngineRequest implements Serializable {

    private static final long serialVersionUID = 1L;
    private IrcMessageEvent ircEvent;

    private long myPid;

    public EngineRequest(IrcMessageEvent ircEvent) {
        this.ircEvent = ircEvent;
    }

    public IrcMessageEvent getIrcEvent() {
        return ircEvent;
    }

    public void setIrcEvent(IrcMessageEvent ircEvent) {
        this.ircEvent = ircEvent;
    }

    public long getMyPid() {
        return myPid;
    }

    public void setMyPid(long myPid) {
        this.myPid = myPid;
    }

}
