package org.freakz.hokan_ng_springboot.bot.common.models;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Petri Airio (petri.j.airio@gmail.com) 09/11/2016 / 13.15
 */
public class ChannelSetTopic implements Serializable {

    private String topic;
    private String channel;
    private String network;
    private String sender;
    private Date timestamp;

    public ChannelSetTopic() {
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getNetwork() {
        return network;
    }

    public void setNetwork(String network) {
        this.network = network;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
