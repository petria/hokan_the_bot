package org.freakz.hokan_ng_springboot.bot.common.events;

import java.io.Serializable;

/**
 * Created by Petri Airio on 25.5.2015.
 */
public class NotifyRequest implements Serializable {

    private long targetChannelId;
    private String notifyMessage;
    private String notifyType;

    public NotifyRequest() {
    }

    public long getTargetChannelId() {
        return targetChannelId;
    }

    public void setTargetChannelId(long id) {
        this.targetChannelId = id;
    }

    public String getNotifyMessage() {
        return notifyMessage;
    }

    public void setNotifyMessage(String notifyMessage) {
        this.notifyMessage = notifyMessage;
    }

    public String getNotifyType() {
        return notifyType;
    }

    public void setNotifyType(String notifyType) {
        this.notifyType = notifyType;
    }
}
