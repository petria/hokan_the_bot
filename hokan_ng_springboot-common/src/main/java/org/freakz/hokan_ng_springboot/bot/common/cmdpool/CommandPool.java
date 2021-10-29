package org.freakz.hokan_ng_springboot.bot.common.cmdpool;

import java.util.List;

/**
 * User: petria
 * Date: 11/5/13
 * Time: 12:13 PM
 *
 * @author Petri Airio <petri.j.airio@gmail.com>
 */
public interface CommandPool {

    void startRunnable(CommandRunnable runnable, String startedBy, Object args);

    void startRunnable(CommandRunnable runnable, String startedBy);

    void startSyncRunnable(CommandRunnable runnable, String startedBy, Object args);

    void runnerFinished(CommandRunner runner, Exception error);

    List<CommandRunner> getActiveRunners();

}
