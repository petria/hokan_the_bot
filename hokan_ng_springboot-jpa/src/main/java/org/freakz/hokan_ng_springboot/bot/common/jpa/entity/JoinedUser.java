package org.freakz.hokan_ng_springboot.bot.common.jpa.entity;

import javax.persistence.*;

/**
 * User: petria
 * Date: 11/11/13
 * Time: 10:19 AM
 *
 * @author Petri Airio <petri.j.airio@gmail.com>
 */
@Entity
@Table(name = "JOINED_USER")
public class JoinedUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private long id;

    @ManyToOne
    @JoinColumn(name = "CHANNEL", referencedColumnName = "ID", nullable = false)
    private Channel channel;

    @ManyToOne
    @JoinColumn(name = "USER", referencedColumnName = "ID", nullable = false)
    private User user;

    @Column(name = "USER_MODES")
    private String userModes;

    public JoinedUser(Channel channel, User user, String userModes) {
        this(channel, user);
        this.userModes = userModes;
    }

    public JoinedUser(Channel channel, User user) {
        this.channel = channel;
        this.user = user;
    }

    public JoinedUser() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getUserModes() {
        return userModes;
    }

    public void setUserModes(String userModes) {
        this.userModes = userModes;
    }

    public boolean isOp() {
        if (this.userModes != null) {
            return this.userModes.contains("@");
        }
        return false;
    }

    public boolean hasVoice() {
        if (this.userModes != null) {
            return this.userModes.contains("+");
        }
        return false;
    }

}
