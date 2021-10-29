package org.freakz.hokan_ng_springboot.bot.common.events;

import java.io.Serializable;

public class SendSMSRequest implements Serializable {

    private String target;
    private String message;

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
