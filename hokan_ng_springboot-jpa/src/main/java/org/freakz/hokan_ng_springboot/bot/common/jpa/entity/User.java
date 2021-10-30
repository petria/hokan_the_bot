package org.freakz.hokan_ng_springboot.bot.common.jpa.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

/**
 * User: petria
 * Date: 3/1/11
 * Time: 2:17 PM
 */
@Entity
@Table(name = "USER")
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private long id;

    @Column(name = "NICK")
    private String nick;

    @Column(name = "PASSWORD")
    private String password;

    @Column(name = "MASK")
    private String mask;

    @Column(name = "REAL_MASK")
    private String realMask;

    @Column(name = "FLAGS", length = 1024, nullable = false)
    private String flags;

    @Column(name = "FULL_NAME")
    private String fullName;

    @Column(name = "PHONE")
    private String phone;

    @Column(name = "EMAIL")
    private String email;

    @Column(name = "LOGGED_IN")
    private int loggedIn;

    @Column(name = "TELEGRAM_ID")
    private int telegramID;

    public User() {
        this.nick = "";
        this.password = "";
        this.fullName = "";
        this.email = "";
        this.phone = "";
        this.flags = "";
        this.mask = "";
        this.realMask = "";
        this.loggedIn = 0;
        this.telegramID = 0;
    }

    public User(String nick) {
        this();
        this.nick = nick;
    }

    public long getId() {
        return id;
    }

    public void setUserId(long id) {
        this.id = id;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMask() {
        return mask;
    }

    public void setMask(String mask) {
        this.mask = mask;
    }

    public String getFlags() {
        return flags;
    }

    public void setFlags(String flags) {
        this.flags = flags;
    }

    public Set<UserFlag> getUserFlagsSet() {
        return UserFlag.getFlagSetFromUser(this);
    }

    public String getFlagsString() {
        return UserFlag.getStringFromFlagSet(this);
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int isLoggedIn() {
        return loggedIn;
    }

    public void setLoggedIn(int loggedIn) {
        this.loggedIn = loggedIn;
    }

    public String getRealMask() {
        return realMask;
    }

    public void setRealMask(String realMask) {
        this.realMask = realMask;
    }

    public String toString() {
        return String.format("[%03d] %s (%s)", this.id, this.nick, this.fullName);
    }

    public int getTelegramID() {
        return telegramID;
    }

    public void setTelegramID(int telegramID) {
        this.telegramID = telegramID;
    }
}
