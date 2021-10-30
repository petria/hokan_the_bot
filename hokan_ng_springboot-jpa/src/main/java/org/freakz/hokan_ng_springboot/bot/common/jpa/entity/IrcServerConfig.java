package org.freakz.hokan_ng_springboot.bot.common.jpa.entity;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Date: 3.6.2013
 * Time: 10:39
 *
 * @author Petri Airio (petri.j.airio@gmail.com)
 */
@Entity
@Table(name = "IRC_SERVER_CONFIG")
public class IrcServerConfig implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private long id;

    @OneToOne
    @JoinColumn(name = "NETWORK_ID_FK", referencedColumnName = "ID")
    private Network network;

    @Column(name = "SERVER")
    private String server;

    @Column(name = "SERVER_PASSWORD")
    private String serverPassword;

    @Column(name = "SERVER_PORT")
    private int port;

    @Column(name = "LOCAL_ADDRESS")
    private String localAddress;

    @Column(name = "USE_THROTTLE")
    private int useThrottle;

    @Column(name = "STATE", nullable = false)
    @Enumerated(EnumType.STRING)
    private IrcServerConfigState ircServerConfigState;

    public IrcServerConfig() {
        this.ircServerConfigState = IrcServerConfigState.DISCONNECTED;
    }

    public Network getNetwork() {
        return network;
    }

    public void setNetwork(Network network) {
        this.network = network;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getServerPassword() {
        return serverPassword;
    }

    public void setServerPassword(String serverPassword) {
        this.serverPassword = serverPassword;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getLocalAddress() {
        return localAddress;
    }

    public void setLocalAddress(String localAddress) {
        this.localAddress = localAddress;
    }

    public int getUseThrottle() {
        return useThrottle;
    }

    public void setUseThrottle(int useThrottle) {
        this.useThrottle = useThrottle;
    }

    public boolean isThrottleInUse() {
        return getUseThrottle() > 0;
    }

    public IrcServerConfigState getIrcServerConfigState() {
        return ircServerConfigState;
    }

    public void setIrcServerConfigState(IrcServerConfigState ircServerConfigState) {
        this.ircServerConfigState = ircServerConfigState;
    }

    public String toString() {
        return String.format("[(%d) %s %s:%d(%s) throttle: %s]",
                getId(), this.network, this.server, this.port, this.serverPassword, isThrottleInUse());
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
