package org.freakz.hokan_ng_springboot.bot.common.jpa.entity;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.io.Serializable;

/**
 * User: petria
 * Date: 3/4/11
 * Time: 10:02 AM
 */
@Entity
@Table(name = "CHANNEL")
public class Channel implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private long id;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "NETWORK_ID_FK", referencedColumnName = "ID")
    private Network network;

    @Column(name = "CHANNEL_NAME")
    private String channelName;

    @Column(name = "CHANNEL_FLAGS")
    private String channelFlags;

    @Column(name = "CHANNEL_STATE", nullable = false)
    @Enumerated(EnumType.STRING)
    private ChannelState channelState;

    @Column(name = "CHANNEL_STARTUP_STATE", nullable = false)
    @Enumerated(EnumType.STRING)
    private ChannelStartupState channelStartupState;

    public Channel(Network network, String name) {
        this.network = network;
        this.channelName = name;
        this.channelStartupState = ChannelStartupState.NO_JOIN;
        this.channelState = ChannelState.NOT_JOINED;
    }

    public Channel() {
        this.channelStartupState = ChannelStartupState.NO_JOIN;
        this.channelState = ChannelState.NOT_JOINED;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Network getNetwork() {
        return network;
    }

    public void setNetwork(Network network) {
        this.network = network;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public String getChannelFlags() {
        return channelFlags;
    }

    public void setChannelFlags(String channelFlags) {
        this.channelFlags = channelFlags;
    }

    public ChannelState getChannelState() {
        return channelState;
    }

    public void setChannelState(ChannelState channelState) {
        this.channelState = channelState;
    }

    public ChannelStartupState getChannelStartupState() {
        return channelStartupState;
    }

    public void setChannelStartupState(ChannelStartupState channelStartupState) {
        this.channelStartupState = channelStartupState;
    }

    public String toString() {
        if (network == null) {
            return String.format("[%02d] %s", id, channelName);
        }
        return String.format("[%02d] %s@%s", id, channelName, network.getName());
    }
}
