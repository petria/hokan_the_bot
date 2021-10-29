package org.freakz.hokan_ng_springboot.bot.io.ircengine;

import org.freakz.hokan_ng_springboot.bot.common.events.EngineResponse;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Created by Petri Airio on 26.8.2015.
 * -
 */
public class ConfirmResponse implements Serializable {

    private LocalDateTime created;
    private EngineResponse response;

    public ConfirmResponse(EngineResponse response) {
        this.response = response;
        this.created = LocalDateTime.now();
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public EngineResponse getResponse() {
        return response;
    }

    public void setResponse(EngineResponse response) {
        this.response = response;
    }

    @Override
    public String toString() {
        return String.format("%s: %s", created.toString(),
                response.getIrcMessageEvent().getSender());
    }
}
