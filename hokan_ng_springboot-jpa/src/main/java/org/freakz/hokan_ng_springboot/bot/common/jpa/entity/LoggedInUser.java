package org.freakz.hokan_ng_springboot.bot.common.jpa.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by Petri Airio on 13.5.2015.
 */
@Entity
@Table(name = "LOGGED_IN_USER")
public class LoggedInUser implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private long id;

    @ManyToOne
    @JoinColumn(name = "USER_ID_FK", referencedColumnName = "ID", nullable = false)
    private User user;

    @Column(name = "LOGIN_TIME")
    private Date loginTime;

    @Column(name = "CONNECTED_FROM")
    private String connectedFrom;

    @Column(name = "LOGGED_IN_STATE", nullable = false)
    @Enumerated(EnumType.STRING)
    private LoggedInState loggedInState;

    public LoggedInUser() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(Date loginTime) {
        this.loginTime = loginTime;
    }

    public String getConnectedFrom() {
        return connectedFrom;
    }

    public void setConnectedFrom(String connectedFrom) {
        this.connectedFrom = connectedFrom;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LoggedInState getLoggedInState() {
        return loggedInState;
    }

    public void setLoggedInState(LoggedInState loggedInState) {
        this.loggedInState = loggedInState;
    }
}
