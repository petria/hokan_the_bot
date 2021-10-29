package org.freakz.hokan_ng_springboot.bot.common.models;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Petri Airio on 17.6.2015.
 */
public class DataUpdaterModel implements Serializable {

    protected long dataFetched = 0;
    protected long itemsFetched = 0;
    protected long itemCount = 0;
    protected long lastUpdateRuntime = 0;
    protected long totalUpdateRuntime = 0;
    private String name;
    private UpdaterStatus status;
    private long count;
    private Date nextUpdate;
    private Date LastUpdate;

    public DataUpdaterModel() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UpdaterStatus getStatus() {
        return status;
    }

    public void setStatus(UpdaterStatus status) {
        this.status = status;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public long getDataFetched() {
        return dataFetched;
    }

    public void setDataFetched(long dataFetched) {
        this.dataFetched = dataFetched;
    }

    public long getItemsFetched() {
        return itemsFetched;
    }

    public void setItemsFetched(long itemsFetched) {
        this.itemsFetched = itemsFetched;
    }

    public long getItemCount() {
        return itemCount;
    }

    public void setItemCount(long itemCount) {
        this.itemCount = itemCount;
    }

    public long getLastUpdateRuntime() {
        return lastUpdateRuntime;
    }

    public void setLastUpdateRuntime(long lastUpdateRuntime) {
        this.lastUpdateRuntime = lastUpdateRuntime;
    }

    public long getTotalUpdateRuntime() {
        return totalUpdateRuntime;
    }

    public void setTotalUpdateRuntime(long totalUpdateRuntime) {
        this.totalUpdateRuntime = totalUpdateRuntime;
    }

    public Date getNextUpdate() {
        return nextUpdate;
    }

    public void setNextUpdate(Date nextUpdate) {
        this.nextUpdate = nextUpdate;
    }

    public Date getLastUpdate() {
        return LastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        LastUpdate = lastUpdate;
    }
}
