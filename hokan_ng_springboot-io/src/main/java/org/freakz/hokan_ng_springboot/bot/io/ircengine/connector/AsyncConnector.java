package org.freakz.hokan_ng_springboot.bot.io.ircengine.connector;

import org.freakz.hokan_ng_springboot.bot.common.cmdpool.CommandPool;
import org.freakz.hokan_ng_springboot.bot.common.cmdpool.CommandRunnable;
import org.freakz.hokan_ng_springboot.bot.common.exception.HokanException;
import org.freakz.hokan_ng_springboot.bot.common.jpa.entity.IrcServerConfig;
import org.freakz.hokan_ng_springboot.bot.io.ircengine.HokanCore;
import org.jibble.pircbot.NickAlreadyInUseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.net.InetAddress;

/**
 * Date: 11/1/13
 * Time: 12:56 PM
 *
 * @author Petri Airio (petri.j.airio@gmail.com)
 */
@Component
@Scope("prototype")
public class AsyncConnector implements Connector, CommandRunnable {

    private static final Logger log = LoggerFactory.getLogger(AsyncConnector.class);

    private HokanCore engine = null;

    private CommandPool commandPool;
    private String botNick;
    private EngineConnector engineConnector;
    private IrcServerConfig configuredServer;

    private boolean aborted = false;

    public AsyncConnector() {
    }

    @Autowired
    public void setEngine(HokanCore engine) {
        this.engine = engine;
    }

    @Autowired
    public void setCommandPool(CommandPool commandPool) {
        this.commandPool = commandPool;
    }

    @Override
    public void abortConnect() {
        this.aborted = true;
    }

    @Override
    public void connect(String nick, EngineConnector engineConnector, IrcServerConfig configuredServer) {
        this.botNick = nick;
        this.engineConnector = engineConnector;
        this.configuredServer = configuredServer;
        commandPool.startRunnable(this, "<system>");
    }

    @Override
    public void handleRun(long myPid, Object args) {
        int tryCount = 5; // TODO get from properties
        String server = this.configuredServer.getServer();
        int serverPort = this.configuredServer.getPort();
        String serverPassword = this.configuredServer.getServerPassword();
        String localAddress = this.configuredServer.getLocalAddress();

        boolean connectOk = false;
        int connectAttemps = 0;
        InetAddress inetAddress = null;
        while (tryCount > 0 && !aborted) {
            connectAttemps++;
//			engine = context.getBean(HokanCore.class);

            try {

                if (localAddress != null) {
                    inetAddress = InetAddress.getByName(localAddress);
                }

                engine.init(this.botNick, this.configuredServer);

                if (serverPassword == null || serverPassword.length() == 0) {
                    engine.connect(server, serverPort, inetAddress);
                } else {
                    engine.connect(server, serverPort, serverPassword, inetAddress);
                }
                connectOk = true;

            } catch (NickAlreadyInUseException e) {
                engine.disconnect();
                this.botNick = String.format("_%s_", botNick);

            } catch (Exception e) {
                String message = e.getMessage();
                if (message.contains("Nickname too long")) {
                    this.botNick = this.botNick.substring(1);
                }
                if (engine != null) {
                    engine.disconnect();
                }
            }
            if (connectOk) {
                aborted = true;
                try {
                    this.engineConnector.engineConnectorGotOnline(this, engine);
                } catch (HokanException e) {
                    e.printStackTrace(); // TODO handle
                }
            } else {
                tryCount--;
                if (tryCount == 0) {
                    this.engineConnector.engineConnectorTooManyConnectAttempts(this, this.configuredServer);
                    aborted = true;
                } else {
                    try {
                        int sleep = 5000 + ((connectAttemps - 1) * 1000);
                        log.info("[" + connectAttemps + "] Sleeping " + (sleep / 1000) + " seconds");
                        Thread.sleep(sleep);
                        log.info("Trying again: " + connectAttemps);
                    } catch (InterruptedException e) { /* do nothing */}
                }
            }
        } // while

    }

    public String toString() {
        return String.format("%s: [%s %s:%d]", this.getClass().toString(), this.configuredServer.getNetwork(),
                this.configuredServer.getServer(), this.configuredServer.getPort());
    }

}
