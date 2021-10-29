package org.freakz.hokan_ng_springboot.bot.common.util;

import org.freakz.hokan_ng_springboot.bot.common.api.DirectoryChangedHandler;
import org.freakz.hokan_ng_springboot.bot.common.api.DirectoryWatcher;
import org.freakz.hokan_ng_springboot.bot.common.cmdpool.CommandPool;
import org.freakz.hokan_ng_springboot.bot.common.cmdpool.CommandRunnable;
import org.freakz.hokan_ng_springboot.bot.common.exception.HokanException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.nio.file.*;
import java.util.concurrent.TimeUnit;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;

/**
 * Created by Petri Airio (petri.j.airio@gmail.com) 01/09/16 / 14:18
 */
@Component
@Scope("prototype")

@SuppressWarnings("unchecked")
public class DirectoryWatcherImpl implements DirectoryWatcher {

    private static final Logger log = LoggerFactory.getLogger(DirectoryWatcherImpl.class);

    private final Object sync = "Sync";
    @Autowired
    private CommandPool commandPool;
    private boolean waitingForChange = false;

    public void waitForChange() {
        try {
            waitingForChange = true;
            synchronized (sync) {
                sync.wait();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public void startWatching(final DirectoryChangedHandler directoryChangedHandler, String watchFolder) {
        final Path path = Paths.get(watchFolder);
        log.debug("Watching folder: " + path);

        CommandRunnable runnable = new CommandRunnable() {
            @Override
            public void handleRun(long myPid, Object args) throws HokanException {
                WatchService service;
                try {
                    FileSystem fs = path.getFileSystem();
                    service = fs.newWatchService();
                    path.register(service, ENTRY_CREATE);
                } catch (Exception e) {
                    log.error("watch", e);
                    return;
                }

                // Start the infinite polling loop
                WatchKey key = null;
                while (true) {
                    try {
                        key = service.poll(1, TimeUnit.SECONDS);
                    } catch (InterruptedException e) {
                        //
                    }
                    if (key == null) {
                        continue;
                    }

                    // Dequeueing events
                    WatchEvent.Kind<?> kind;
                    for (WatchEvent<?> watchEvent : key.pollEvents()) {
                        // Get the type of the event
                        kind = watchEvent.kind();
                        if (ENTRY_CREATE == kind) {
                            Path newPath = ((WatchEvent<Path>) watchEvent).context();
                            String file = path.toString() + "/" + newPath.getFileName();
                            try {
                                directoryChangedHandler.fileCreated(file);
                                if (waitingForChange) {
                                    synchronized (sync) {
                                        sync.notify();
                                    }
                                }
                            } catch (Exception e) {
                                log.error("Load telkku data", e);
                            }
                        }
                    }

                    if (!key.reset()) {
                        break; //loop
                    }
                }

            }
        };
        commandPool.startRunnable(runnable, "<system>");

    }


}
