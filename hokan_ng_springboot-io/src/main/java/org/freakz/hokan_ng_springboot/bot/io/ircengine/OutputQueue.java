package org.freakz.hokan_ng_springboot.bot.io.ircengine;

import org.freakz.hokan_ng_springboot.bot.common.cmdpool.CommandPool;
import org.freakz.hokan_ng_springboot.bot.common.cmdpool.CommandRunnable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * User: petria
 * Date: 11/17/13
 * Time: 6:28 PM
 *
 * @author Petri Airio <petri.j.airio@gmail.com>
 */
@Component
@Scope("prototype")
public class OutputQueue implements CommandRunnable {

    private static final Logger log = LoggerFactory.getLogger(OutputQueue.class);

    @Autowired
    private CommandPool commandPool;

    private final Queue<String> outQueue = new ConcurrentLinkedQueue<>();
    private HokanCore core;

    public int defSleepTime = 300;
    public int defMaxLines = 4;
    public int defFullLineLength = 400;
    public int defFullLineSleepTime = 5000;
    public int defThrottleBaseSleepTime = 2000;

    private boolean useThrottle;
    private boolean stop = false;

    public OutputQueue() {
    }

    public void init(HokanCore core, boolean useThrottle) {
        this.core = core;
        this.useThrottle = useThrottle;
        this.commandPool.startRunnable(this, "<system>");
    }

    @Override
    public void handleRun(long myPid, Object args) {

        log.info("<< Starting dispatcher thread: {}", myPid);

        boolean throttle = useThrottle;
        long sleepTime = defSleepTime;

        while (core.isConnected()) {

            int linesSent = 0;

            while (!outQueue.isEmpty()) {
                String rawLine = outQueue.poll();
                if (stop) {
                    log.info("STOP!");
                    return;
                }
                int length = rawLine.length();
                core.sendRawLine(rawLine);
                linesSent++;
                if (useThrottle) {
                    if (length > defFullLineLength) {
                        throttle = true;
                        sleepTime = defFullLineSleepTime;
                        break;
                    }
                    if (linesSent > defMaxLines) {
                        throttle = true;
                        break;
                    }
                    if (throttle) {
                        sleepTime = defThrottleBaseSleepTime * (1 + (length / 100));
                        break;
                    }
                }
            }

            try {
                //        LOG.info(Thread.currentThread() + " - SLEEP: " + sTime + " --> throttle: " + throttle);
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                // ignore
            }

            if (useThrottle) {
                if (throttle && outQueue.isEmpty()) {
                    sleepTime = defSleepTime;
                    throttle = false;
                }
            }

        }

        log.info("<< Exiting dispatcher thread >>");
    }

    public void addLine(String raw) {
        this.outQueue.add(raw);
    }

    public void stop() {
        this.stop = true;
        this.outQueue.add("stop");
    }

    public void clearOutQueue() {
        outQueue.clear();
    }

    public void setThrottle(boolean throttle) {
        useThrottle = throttle;
    }

    public boolean isUsingThrottle() {
        return useThrottle;
    }

    public boolean setQueueValues(String values) {

        String[] split = values.split(" ");
        if (split.length != 5) {
            return false;
        }
        int[] newValues = new int[5];
        for (int i = 0; i < newValues.length; i++) {
            try {
                newValues[i] = Integer.parseInt(split[i]);

            } catch (NumberFormatException ex) {
                return false;
            }
        }
        defSleepTime = newValues[0];
        defMaxLines = newValues[1];
        defFullLineLength = newValues[2];
        defFullLineSleepTime = newValues[3];
        defThrottleBaseSleepTime = newValues[4];

        return true;
    }

}
