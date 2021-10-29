package org.freakz.hokan_ng_springboot.bot.common.cmdpool;


import org.freakz.hokan_ng_springboot.bot.common.jpa.service.PropertyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * User: petria
 * Date: 11/5/13
 * Time: 12:04 PM
 *
 * @author Petri Airio <petri.j.airio@gmail.com>
 */
@Component
public class CommandPoolImpl implements CommandPool, DisposableBean {

    private static final Logger log = LoggerFactory.getLogger(CommandPoolImpl.class);

    @Autowired
    private PropertyService propertyService;

    private ExecutorService executor = Executors.newCachedThreadPool();
    private List<CommandRunner> activeRunners = new ArrayList<>();

    public CommandPoolImpl() {
    }


    @Override
    public void startRunnable(CommandRunnable runnable, String startedBy) {
        startRunnable(runnable, startedBy, null);
    }

    @Override
    public void startRunnable(CommandRunnable runnable, String startedBy, Object args) {
        long pid = propertyService.getNextPid();
        CommandRunner runner = new CommandRunner(pid, runnable, this, args);
        activeRunners.add(runner);
        this.executor.execute(runner);
    }

    @Override
    public void startSyncRunnable(CommandRunnable runnable, String startedBy, Object args) {
        long pid = propertyService.getNextPid();
        CommandRunner runner = new CommandRunner(pid, runnable, this, args);
        activeRunners.add(runner);
        runner.run();
    }

    @Override
    public void runnerFinished(CommandRunner runner, Exception error) {
        this.activeRunners.remove(runner);
    }

    @Override
    public List<CommandRunner> getActiveRunners() {
        return this.activeRunners;
    }

    @Override
    public void destroy() throws Exception {
        List<Runnable> runnableList = executor.shutdownNow();
        log.info("Runnable size: {}", runnableList.size());
    }

}
