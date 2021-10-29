package org.freakz.hokan_ng_springboot.bot.services.updaters;


import org.freakz.hokan_ng_springboot.bot.common.cmdpool.CommandPool;
import org.freakz.hokan_ng_springboot.bot.common.models.UpdaterStatus;

import java.util.Calendar;

/**
 * User: petria
 * Date: 11/18/13
 * Time: 2:25 PM
 *
 * @author Petri Airio <petri.j.airio@gmail.com>
 */
public interface DataUpdater {

    Calendar getNextUpdateTime();

    Calendar getLastUpdateTime();

    Calendar calculateNextUpdate();

    void updateData(CommandPool commandPool);

    void getData(UpdaterData updaterData, Object... args);

    UpdaterData getData(Object... args);

    UpdaterStatus getStatus();

    long getUpdateCount();

    long getDataFetched();

    long getItemsFetched();

    long getItemmCount();

    long getLastUpdateRuntime();

    long getTotalUpdateRuntime();

    String getUpdaterName();


}
