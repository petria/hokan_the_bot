package org.freakz.hokan_ng_springboot.bot.common.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class NatoRatifyStats implements Serializable {

    private List<String> ratified = new ArrayList<>();
    private List<String> notRatified = new ArrayList<>();
    private String summary;
    private String updated;

    public List<String> getRatified() {
        return ratified;
    }

    public void setRatified(List<String> ratified) {
        this.ratified = ratified;
    }

    public List<String> getNotRatified() {
        return notRatified;
    }

    public void setNotRatified(List<String> notRatified) {
        this.notRatified = notRatified;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getUpdated() {
        return updated;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
    }
}
