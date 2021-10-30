package org.freakz.hokan_ng_springboot.bot.common.jpa.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by pairio on 28.5.2014.
 *
 * @author Petri Airio (petri.j.airio@gmail.com)
 */
@Entity
@Table(name = "SEARCH_REPLACE")
public class SearchReplace implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private long id;

    @Column(name = "OWNER")
    private String owner;

    @Column(name = "THE_SEARCH")
    private String theSearch;

    @Column(name = "THE_REPLACE")
    private String theReplace;

    @Column(name = "CREATED")
    private Date created;

    public SearchReplace() {
    }

    public SearchReplace(String owner, String search, String replace) {
        this();
        this.owner = owner;
        this.theSearch = search;
        this.theReplace = replace;
        this.created = new Date();
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getSearch() {
        return theSearch;
    }

    public void setSearch(String search) {
        this.theSearch = search;
    }

    public String getReplace() {
        return theReplace;
    }

    public void setReplace(String replace) {
        this.theReplace = replace;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }
}
