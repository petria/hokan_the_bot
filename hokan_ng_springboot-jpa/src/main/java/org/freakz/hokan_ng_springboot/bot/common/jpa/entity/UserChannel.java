package org.freakz.hokan_ng_springboot.bot.common.jpa.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Date: 15.11.2012
 * Time: 15:03
 *
 * @author Petri Airio (petri.j.airio@gmail.com)
 */
@Entity
@Table(name = "USER_CHANNEL")
public class UserChannel implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private long userChannelId;

    @ManyToOne
    @JoinColumn(name = "CHANNEL_ID_FK", referencedColumnName = "ID", nullable = false)
    private Channel channel;

    @ManyToOne
    @JoinColumn(name = "USER_ID_FK", referencedColumnName = "ID", nullable = false)
    private User user;

    @Column(name = "JOIN_COMMENT")
    private String joinComment;

    @Column(name = "CHANNEL_OP")
    private int channelOp;

    @Column(name = "LAST_JOIN")
    private Date lastJoin;

    @Column(name = "LAST_PART")
    private Date lastPart;

    @Column(name = "LAST_PART_MESSAGE")
    private String lastPartMessage;

    @Column(name = "LAST_IRC_LOG_ID")
    private String lastIrcLogID;

    @Column(name = "LAST_MESSAGE_TIME")
    private Date lastMessageTime;

    @Column(name = "LAST_COMMAND", length = 256)
    private String lastCommand;

    @Column(name = "LAST_COMMAND_TIME")
    private Date lastCommandTime;


    public UserChannel() {
        this.joinComment = "";
        this.channelOp = 0;
        this.lastJoin = new Date();
        this.lastPart = new Date();
        this.lastPartMessage = "";
        this.lastMessageTime = new Date();
        this.lastCommand = "";
        this.lastCommandTime = new Date();
    }

    public UserChannel(User user, Channel channel) {
        this();
        this.user = user;
        this.channel = channel;
    }

    public long getUserChannelId() {
        return userChannelId;
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

    public String getJoinComment() {
        return joinComment;
    }

    public void setJoinComment(String joinComment) {
        this.joinComment = joinComment;
    }

    public boolean isChannelOp() {
        return channelOp > 0;
    }

    public void setChannelOp(boolean channelOp) {
        if (channelOp) {
            this.channelOp = 1;
        } else {
            this.channelOp = 0;
        }
    }

    public Date getLastJoin() {
        return lastJoin;
    }

    public void setLastJoin(Date lastJoin) {
        this.lastJoin = lastJoin;
    }

    public Date getLastPart() {
        return lastPart;
    }

    public void setLastPart(Date lastPart) {
        this.lastPart = lastPart;
    }

    public String getLastPartMessage() {
        return lastPartMessage;
    }

    public void setLastPartMessage(String lastPartMessage) {
        this.lastPartMessage = lastPartMessage;
    }

    public String getLastIrcLogID() {
        return lastIrcLogID;
    }

    public void setLastIrcLogID(String lastIrcLogID) {
        this.lastIrcLogID = lastIrcLogID;
    }

    public Date getLastMessageTime() {
        return lastMessageTime;
    }

    public void setLastMessageTime(Date lastMessageTime) {
        this.lastMessageTime = lastMessageTime;
    }

    public String getLastCommand() {
        return lastCommand;
    }

    public void setLastCommand(String lastCommand) {
        this.lastCommand = lastCommand;
    }

    public Date getLastCommandTime() {
        return lastCommandTime;
    }

    public void setLastCommandTime(Date lastCommandTime) {
        this.lastCommandTime = lastCommandTime;
    }


}
