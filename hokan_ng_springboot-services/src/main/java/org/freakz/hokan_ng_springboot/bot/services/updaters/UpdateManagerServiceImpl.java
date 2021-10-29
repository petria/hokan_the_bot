package org.freakz.hokan_ng_springboot.bot.services.updaters;

import org.freakz.hokan_ng_springboot.bot.common.cmdpool.CommandPool;
import org.freakz.hokan_ng_springboot.bot.common.cmdpool.CommandRunnable;
import org.freakz.hokan_ng_springboot.bot.common.models.DataUpdaterModel;
import org.freakz.hokan_ng_springboot.bot.common.models.UpdaterStatus;
import org.freakz.hokan_ng_springboot.bot.common.util.StringStuff;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;

/**
 * User: petria
 * Date: 11/18/13
 * Time: 8:19 PM
 *
 * @author Petri Airio <petri.j.airio@gmail.com>
 */
@Service
public class UpdateManagerServiceImpl implements UpdaterManagerService, CommandRunnable {

    private static final Logger log = LoggerFactory.getLogger(UpdateManagerServiceImpl.class);

    @Autowired
    private ApplicationContext context;

    @Autowired
    private CommandPool commandPool;

    private Map<String, DataUpdater> handlers;
    private boolean doRun;
    private boolean firstRun = true;

    public UpdateManagerServiceImpl() {
        log.info("START!");
    }

    @PostConstruct
    public void refreshHandlers() {
        handlers = context.getBeansOfType(DataUpdater.class);
        start();
    }


    public void stop() {
        this.doRun = false;
    }

    @Override
    public void start() {
        commandPool.startRunnable(this, "<system>");
    }

    @Override
    public List<DataUpdaterModel> getDataUpdaterModelList() {
        List<DataUpdaterModel> modelList = new ArrayList<>();
        for (DataUpdater updater : this.handlers.values()) {
            DataUpdaterModel model = new DataUpdaterModel();

            model.setName(updater.getUpdaterName());
            model.setStatus(updater.getStatus());
            model.setCount(updater.getUpdateCount());
            model.setDataFetched(updater.getDataFetched());
            model.setItemsFetched(updater.getItemsFetched());
            model.setItemCount(updater.getItemmCount());
            model.setLastUpdateRuntime(updater.getLastUpdateRuntime());
            model.setTotalUpdateRuntime(updater.getTotalUpdateRuntime());

            if (updater.getNextUpdateTime() != null) {
                model.setNextUpdate(updater.getNextUpdateTime().getTime());
            }
            if (updater.getLastUpdateTime() != null) {
                model.setLastUpdate(updater.getLastUpdateTime().getTime());
            }
            modelList.add(model);
        }
        return modelList;
    }

    @Override
    public List<DataUpdater> getUpdaterList() {
        return new ArrayList<>(this.handlers.values());
    }

    public DataUpdater getUpdater(String updaterName) {
        for (DataUpdater updater : handlers.values()) {
            if (StringStuff.match(updater.getUpdaterName(), updaterName, true)) {
                return updater;
            }
        }
        return null;
    }

    @Override
    public void startUpdater(DataUpdater updater) {
        log.info("Starting updater: " + updater);
        updater.updateData(this.commandPool);
    }

    @Override
    public DataUpdaterModel startUpdaterByName(String updaterName) {
        DataUpdater updater = getUpdater(updaterName);
        if (updater != null) {
            startUpdater(updater);
            DataUpdaterModel model = new DataUpdaterModel();
            model.setName(updater.getUpdaterName());
            model.setStatus(updater.getStatus());
            model.setCount(updater.getUpdateCount());
            model.setNextUpdate(updater.getNextUpdateTime().getTime());
            model.setLastUpdate(updater.getLastUpdateTime().getTime());
            return model;
        }
        return null;
    }

    @Override
    public void handleRun(long myPid, Object args) {
        doRun = true;
        log.info("<< Starting update service: {} >>", myPid);
        while (doRun) {
            for (DataUpdater updater : getUpdaterList()) {
                if (updater.getStatus() == UpdaterStatus.HOST_OS_NOT_SUPPORTED) {
                    continue;
                }
                Calendar now = new GregorianCalendar();
                Calendar next = updater.getNextUpdateTime();
                if (firstRun || now.after(next)) {
                    if (updater.getStatus() != UpdaterStatus.UPDATING) {
                        updater.updateData(this.commandPool);
                    }
                }
            }
            firstRun = false;
            try {
                Thread.sleep(1000 * 10);
            } catch (InterruptedException e) {
                // ignore
            }
        }
        log.info("<< Stopping update service: {} >>", myPid);
    }
}
