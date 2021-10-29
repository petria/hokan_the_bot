package org.freakz.hokan_ng_springboot.bot.common.models;

import java.io.Serializable;

public class TimeDifferenceData implements Serializable {

    private long[] diffs;

    public long[] getDiffs() {
        return diffs;
    }

    public void setDiffs(long[] diffs) {
        this.diffs = diffs;
    }
}
