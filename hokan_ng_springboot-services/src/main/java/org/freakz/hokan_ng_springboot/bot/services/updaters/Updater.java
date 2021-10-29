package org.freakz.hokan_ng_springboot.bot.services.updaters;

import org.freakz.hokan_ng_springboot.bot.common.cmdpool.CommandPool;
import org.freakz.hokan_ng_springboot.bot.common.cmdpool.CommandRunnable;
import org.freakz.hokan_ng_springboot.bot.common.exception.HokanException;
import org.freakz.hokan_ng_springboot.bot.common.exception.HokanHostOsNotSupportedException;
import org.freakz.hokan_ng_springboot.bot.common.models.UpdaterStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * User: petria
 * Date: 11/18/13
 * Time: 2:31 PM
 *
 * @author Petri Airio <petri.j.airio@gmail.com>
 */
@Component
public abstract class Updater implements DataUpdater, CommandRunnable {

    private static final Logger log = LoggerFactory.getLogger(Updater.class);

    protected long updateCount = 0;

    protected long dataFetched = 0;
    protected long itemsFetched = 0;
    protected long itemCount = 0;

    protected long lastUpdateRuntime = 0;
    protected long totalUpdateRuntime = 0;

    private Calendar nextUpdate = new GregorianCalendar();
    private Calendar lastUpdate;
    protected UpdaterStatus status;

    protected Updater() {
    }

    @Override
    public String getUpdaterName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public Calendar getNextUpdateTime() {
        return nextUpdate;
    }

    @Override
    public Calendar getLastUpdateTime() {
        return lastUpdate;
    }

    @Override
    public void updateData(CommandPool commandPool) {
        commandPool.startRunnable(this, "<system>");
    }

    @Override
    public void handleRun(long myPid, Object args) throws HokanException {
        try {
            status = UpdaterStatus.UPDATING;
            long startTime = System.currentTimeMillis();
            doUpdateData();
            long duration = (System.currentTimeMillis() - startTime) / 1000;
            this.lastUpdateRuntime = duration;
            this.totalUpdateRuntime += duration;
            nextUpdate = calculateNextUpdate();
            status = UpdaterStatus.IDLE;
        } catch (HokanHostOsNotSupportedException e) {
            status = UpdaterStatus.HOST_OS_NOT_SUPPORTED;
        } catch (Exception e) {
            log.error("Updater failed", e);
            status = UpdaterStatus.CRASHED;
            this.nextUpdate.add(Calendar.MINUTE, 2);
            throw new HokanException("Updater failed, trying again  in 2 minutes!", e);
        } finally {
            updateCount++;
            lastUpdate = new GregorianCalendar();
        }
    }


    public abstract void doUpdateData() throws Exception;

    @Override
    public void getData(UpdaterData data, Object... args) {
        data.setData(doGetData(args));
    }

    @Override
    public UpdaterData getData(Object... args) {
        UpdaterData data = new UpdaterData();
        data.setData(doGetData(args));
        return data;
    }

    public abstract Object doGetData(Object... args);

    @Override
    public UpdaterStatus getStatus() {
        return this.status;
    }

    @Override
    public long getUpdateCount() {
        return this.updateCount;
    }

    @Override
    public long getDataFetched() {
        return this.dataFetched;
    }

    @Override
    public long getItemsFetched() {
        return this.itemsFetched;
    }

    @Override
    public long getItemmCount() {
        return this.itemCount;
    }

    @Override
    public long getLastUpdateRuntime() {
        return this.lastUpdateRuntime;
    }

    @Override
    public long getTotalUpdateRuntime() {
        return this.totalUpdateRuntime;
    }

}
