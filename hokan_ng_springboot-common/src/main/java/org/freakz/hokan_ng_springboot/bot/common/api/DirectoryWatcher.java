package org.freakz.hokan_ng_springboot.bot.common.api;

/**
 * Created by Petri Airio (petri.j.airio@gmail.com) 02/09/16 / 13:05
 */
public interface DirectoryWatcher {

    void startWatching(final DirectoryChangedHandler directoryChangedHandler, String watchFolder);

    void waitForChange();

}
