package org.freakz.hokan_ng_springboot.bot.common.jpa.entity;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Date: 23.1.2012
 * Time: 11:34
 *
 * @author Petri Airio
 */
@Entity
@Table(name = "DATA_VALUES")
public class DataValues implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private long id;

    @Column(name = "NICK")
    private String nick;

    @Column(name = "NETWORK")
    private String network;

    @Column(name = "CHANNEL")
    private String channel;

    @Column(name = "KEYNAME")
    private String keyName;

    @Column(name = "VALUE")
    private String value;


    public DataValues() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getNetwork() {
        return network;
    }

    public void setNetwork(String network) {
        this.network = network;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getKeyName() {
        return keyName;
    }

    public void setKeyName(String key) {
        this.keyName = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
