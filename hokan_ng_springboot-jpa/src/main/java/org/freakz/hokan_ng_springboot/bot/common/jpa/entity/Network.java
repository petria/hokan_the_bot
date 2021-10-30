package org.freakz.hokan_ng_springboot.bot.common.jpa.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: petria
 * Date: 3/20/11
 * Time: 1:31 PM
 */
@Entity
@Table(name = "NETWORK")
public class Network implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private long id;

    @Column(name = "NETWORK_NAME")
    private String networkName;

    @Column(name = "FIRST_CONNECTED")
    private Date firstConnected;

    @Column(name = "CONNECT_COUNT")
    private int connectCount;

    @Column(name = "LINES_SENT")
    private int linesSent;

    @Column(name = "LINES_RECEIVED")
    private int linesReceived;

    @Column(name = "CHANNELS_JOINED")
    private int channelsJoined;

    public Network() {
        this("<NEW_NETWORK>");
    }

    public Network(String name) {
        this.networkName = name;
        this.firstConnected = new Date();
        this.connectCount = 0;
        this.linesSent = 0;
        this.linesReceived = 0;
        this.channelsJoined = 0;
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }


    public String getName() {
        return networkName;
    }

    public void setName(String name) {
        this.networkName = name;
    }

    public Date getFirstConnected() {
        return firstConnected;
    }

    public void setFirstConnected(Date firstConnected) {
        this.firstConnected = firstConnected;
    }

    public int getConnectCount() {
        return connectCount;
    }

    public void setConnectCount(int connectCount) {
        this.connectCount = connectCount;
    }

    public void addToConnectCount(int delta) {
        this.connectCount += delta;
    }

    public int getLinesSent() {
        return linesSent;
    }

    public void setLinesSent(int linesSent) {
        this.linesSent = linesSent;
    }

    public void addToLinesSent(int delta) {
        this.linesSent += delta;
    }

    public int getLinesReceived() {
        return linesReceived;
    }

    public void setLinesReceived(int linesReceived) {
        this.linesReceived = linesReceived;
    }

    public void addToLinesReceived(int delta) {
        this.linesReceived += delta;
    }

    public int getChannelsJoined() {
        return channelsJoined;
    }

    public void setChannelsJoined(int channelsJoined) {
        this.channelsJoined = channelsJoined;
    }

    public void addToChannelsJoined(int delta) {
        this.channelsJoined += delta;
    }

    public String toString() {
        return String.format("[%02d] - %s", this.id, this.networkName);
    }

}
