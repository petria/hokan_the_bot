package org.freakz.hokan_ng_springboot.bot.common.cmdpool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * User: petria
 * Date: 11/5/13
 * Time: 12:09 PM
 *
 * @author Petri Airio <petri.j.airio@gmail.com>
 */

public class CommandRunner implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(CommandRunner.class);

    private long myPid;
    private CommandRunnable runnable;
    private CommandPoolImpl commandPool;
    private Object args;

    public CommandRunner(long myPid, CommandRunnable runnable, CommandPoolImpl commandPool, Object args) {
        this.myPid = myPid;
        this.runnable = runnable;
        this.commandPool = commandPool;
        this.args = args;
    }

    @Override
    public void run() {
        Thread.currentThread().setName("[" + myPid + "] CommandRunner: " + runnable);
        try {
            this.runnable.handleRun(myPid, args);
        } catch (Exception e) {
            log.error("CommandRunner error", e);
        }

    }

    @Override
    public String toString() {
        return String.format("%4d %25s", myPid, runnable.getClass().getSimpleName());
    }

}
