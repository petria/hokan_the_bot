package org.freakz.hokan_ng_springboot.bot.services.updaters;

import org.freakz.hokan_ng_springboot.bot.common.models.DataUpdaterModel;

import java.util.List;

/**
 * User: petria
 * Date: 11/18/13
 * Time: 2:24 PM
 *
 * @author Petri Airio <petri.j.airio@gmail.com>
 */
public interface UpdaterManagerService {

    List<DataUpdaterModel> getDataUpdaterModelList();

    List<DataUpdater> getUpdaterList();

    DataUpdater getUpdater(String updaterName);

    void stop();

    void start();

    void startUpdater(DataUpdater updater);

    DataUpdaterModel startUpdaterByName(String updater);

}
