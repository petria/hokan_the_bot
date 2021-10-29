package org.freakz.hokan_ng_springboot.bot.common.models;

import java.time.LocalDateTime;

/**
 * Created by Petri Airio on 21.8.2015.
 */
public class StartAndEndTime {

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public StartAndEndTime(LocalDateTime startTime, LocalDateTime endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }
}
