package org.freakz.hokan_ng_springboot.bot.io.service;

import org.freakz.hokan_ng_springboot.bot.common.enums.CommandLineArgs;
import org.freakz.hokan_ng_springboot.bot.common.enums.HokanModule;
import org.freakz.hokan_ng_springboot.bot.common.events.*;
import org.freakz.hokan_ng_springboot.bot.common.exception.HokanException;
import org.freakz.hokan_ng_springboot.bot.common.exception.HokanServiceException;
import org.freakz.hokan_ng_springboot.bot.common.jms.JmsMessage;
import org.freakz.hokan_ng_springboot.bot.common.jms.api.JmsSender;
import org.freakz.hokan_ng_springboot.bot.common.jpa.entity.*;
import org.freakz.hokan_ng_springboot.bot.common.jpa.service.*;
import org.freakz.hokan_ng_springboot.bot.common.service.ConnectionManagerService;
import org.freakz.hokan_ng_springboot.bot.common.util.CommandArgs;
import org.freakz.hokan_ng_springboot.bot.common.util.CommandLineArgsParser;
import org.freakz.hokan_ng_springboot.bot.io.ircengine.HokanCore;
import org.freakz.hokan_ng_springboot.bot.io.ircengine.connector.AsyncConnector;
import org.freakz.hokan_ng_springboot.bot.io.ircengine.connector.Connector;
import org.freakz.hokan_ng_springboot.bot.io.ircengine.connector.EngineConnector;
import org.freakz.hokan_ng_springboot.bot.io.service.configinit.ConfigurationInit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by AirioP on 17.2.2015.
 * -
 */
@Service
public class ConnectionManagerServiceImpl implements ConnectionManagerService, EngineConnector, CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(ConnectionManagerServiceImpl.class);

    @Autowired
    private ApplicationContext context;

    @Autowired
    private ChannelService channelService;

    @Autowired
    private ConfigurationInit configurationInit;

    @Autowired
    private IrcServerConfigService ircServerConfigService;

    @Autowired
    private LoggedInUserService loggedInUserService;

    @Autowired
    private NetworkService networkService;

    @Autowired
    private PropertyService propertyService;

    @Autowired
    private UserRepositoryService userService;

    private String botNick;

    private Map<String, HokanCore> connectedEngines = new HashMap<>();
    private Map<String, Connector> connectors = new HashMap<>();
    private Map<String, IrcServerConfig> configuredServers;


    private void invalidateLoggedInUsers() {
        loggedInUserService.invalidateAll();
        userService.setAllLoggedIn(0);
    }

    private void resetChannelStates() {
        channelService.resetChannelStates();
    }

    private void updateServerMap() {
        List<IrcServerConfig> servers = ircServerConfigService.findAll();
        configuredServers = new HashMap<>();
        for (IrcServerConfig server : servers) {
            configuredServers.put(server.getNetwork().getName(), server);
        }
    }

    private boolean botNickOk() {
        try {
            this.botNick = propertyService.findFirstByPropertyName(PropertyName.PROP_SYS_BOT_NICK).getValue();
            if (this.botNick == null) {
                this.botNick = "HokanNG2";
            }
        } catch (Exception e) {
            log.error("Error did occur {}", e);
            return false;
        }
        return botNick.length() > 0;
    }

    public HokanCore getConnectedEngine(Network network) {
        return this.connectedEngines.get(network.getName());
    }

    public void connect(Network network) throws HokanServiceException {

        if (!botNickOk()) {
            throw new HokanServiceException("PropertyName.PROP_SYS_BOT_NICK not configured correctly: " + this.botNick);
        }
        updateServerMap();
        HokanCore engine = getConnectedEngine(network);
        if (engine != null) {
            throw new HokanServiceException("Engine already connected to network: " + engine);
        }

        IrcServerConfig configuredServer = configuredServers.get(network.getName());
        if (configuredServer == null) {
            throw new HokanServiceException("IrcServerConfig not found for network: " + network);
        }
        configuredServer.setIrcServerConfigState(IrcServerConfigState.CONNECTED);
        this.ircServerConfigService.save(configuredServer);

        Connector connector;
        connector = this.connectors.get(configuredServer.getNetwork().getName());
        if (connector == null) {
            connector = context.getBean(AsyncConnector.class);
            this.connectors.put(configuredServer.getNetwork().getName(), connector);
            connector.connect(this.botNick, this, configuredServer);
        } else {
            throw new HokanServiceException("Going online attempt already going: " + configuredServer.getNetwork());
        }

    }

    @Override
    public void joinChannels(String network) throws HokanServiceException {

    }

    @Override
    public void connect(String networkName) throws HokanServiceException {
        Network network = networkService.getNetwork(networkName);
        if (network != null) {
            connect(network);
        } else {
            throw new HokanServiceException("Can't connect to {}, Network not configured: " + networkName);
        }
    }

    @Override
    public void disconnect(String network) throws HokanServiceException {

    }

    @Override
    public void disconnectAll() {

    }

    @Override
    public void updateServers() {

    }

    @Autowired
    private JmsSender jmsSender;

    public ServiceResponse doServicesRequest(ServiceRequestType requestType, IrcMessageEvent ircEvent, Object... parameters) throws HokanException {
        ServiceRequest request = new ServiceRequest(requestType, ircEvent, new CommandArgs(ircEvent.getMessage()), parameters);
        ObjectMessage objectMessage = jmsSender.sendAndGetReply(HokanModule.HokanServices.getQueueName(), "SERVICE_REQUEST", request, false);
        if (objectMessage == null) {
            throw new HokanException("ServiceResponse null, is Services module running?");
        }
        try {
            JmsMessage jmsMessage = (JmsMessage) objectMessage.getObject();
            return jmsMessage.getServiceResponse();
        } catch (JMSException e) {
            log.error("jms", e);
        }
        return new ServiceResponse(requestType);

    }

    //	----- EngineConnector

    @Override
    public void engineConnectorTooManyConnectAttempts(Connector connector, IrcServerConfig configuredServer) {
        this.connectors.remove(configuredServer.getNetwork().getName());

        String message = ("Too many connection attempts:" + connector);
        SendSMSRequest smsRequest = new SendSMSRequest();
        smsRequest.setTarget("3584577345641");
        smsRequest.setMessage(message);
        log.info("Sending alert SMS!");
        IrcMessageEvent ircEvemt = (IrcMessageEvent) IrcEventFactory.createIrcEvent("bot", "dev", "<internal>", "<system>", "login", "localhost");
        try {
            ServiceResponse serviceResponse = doServicesRequest(ServiceRequestType.SMS_SEND_SERVICE_REQUEST, ircEvemt, smsRequest);
        } catch (HokanException e) {
            log.error("SMS Notify failed!");
        }
        log.error(message);
    }

    @Override
    public void engineConnectorTooManyConnections(Connector connector, IrcServerConfig configuredServer) {
        log.info("Too many connections:" + connector);
    }

    @Override
    public void engineConnectorGotOnline(Connector connector, HokanCore engine) throws HokanException {

        IrcServerConfig config = engine.getIrcServerConfig();
        config.setIrcServerConfigState(IrcServerConfigState.CONNECTED);
        this.ircServerConfigService.save(config);
        engine.setIrcServerConfig(config);

        Network network = config.getNetwork();
        if (network.getFirstConnected() == null) {
            network.setFirstConnected(new Date());
        }
        network.addToConnectCount(1);

        engine.startOutputQueue();

        this.connectors.remove(network.getName());
        this.connectedEngines.put(network.getName(), engine);
        this.networkService.save(network);

        joinChannels(engine, network);

    }

    private void joinChannels(HokanCore engine, Network network) {
        List<Channel> channels = this.channelService.findAll();
        if (channels != null) {
            for (Channel channelToJoin : channels) {
                if (channelToJoin.getNetwork().getName().equals(network.getName())) {
                    if (channelToJoin.getChannelStartupState() == ChannelStartupState.JOIN) {
                        log.info("--> joining to {}", channelToJoin.getChannelName());
                        engine.joinChannel(channelToJoin.getChannelName());
                    }
                }
            }
        } else {
            log.info("NO channels to join: {} -> {}", engine, network);
        }
    }

    @Override
    public void engineConnectorDisconnected(HokanCore hokanCore) {
        log.info("Engine disconnected: " + hokanCore);
        try {
            this.connectedEngines.remove(hokanCore.getIrcServerConfig().getNetwork().getName());
            connect(hokanCore.getIrcServerConfig().getNetwork().getName());
        } catch (HokanServiceException e) {
            log.error("Couldn't re-connect after disconnect timeout!", e);
        }
    }

    @Override
    public void engineConnectorPingTimeout(HokanCore hokanCore) {
        log.info("Engine ping timeout: {}", hokanCore);
        try {
            this.connectedEngines.remove(hokanCore.getIrcServerConfig().getNetwork().getName());
            connect(hokanCore.getIrcServerConfig().getNetwork().getName());
        } catch (HokanServiceException e) {
            log.error("Couldn't re-connect after ping timeout!", e);
        }
    }

    @Override
    public void engineConnectorExcessFlood(HokanCore hokanCore) {
        log.info("Engine Excess Flood: {}", hokanCore);
        try {
            this.connectedEngines.remove(hokanCore.getIrcServerConfig().getNetwork().getName());
            connect(hokanCore.getIrcServerConfig().getNetwork().getName());
        } catch (HokanServiceException e) {
            log.error("Couldn't re-connect after Excess Flood!", e);
        }

    }

    @Override
    public void handleEngineResponse(EngineResponse response) {
//    log.debug("Handle: {}", response);
        HokanCore core = this.connectedEngines.get(response.getIrcMessageEvent().getNetwork());
        if (core != null) {
            core.handleEngineResponse(response);
        } else {
            log.warn("No active HokanCore found!");
        }
    }

    @Override
    public void handleTvNotifyRequest(NotifyRequest notifyRequest) {
        Channel channel = channelService.findOne(notifyRequest.getTargetChannelId());
        if (channel == null) {
            log.warn("Can't notify, no channel with id: {}", notifyRequest.getTargetChannelId());
            return;
        }
        HokanCore core = getConnectedEngine(channel.getNetwork());
        if (core == null) {
            log.warn("Can't notify, no HokanCore for channel: {}", channel);
            return;
        }
        core.handleSendMessage(channel.getChannelName(), notifyRequest.getNotifyMessage());
        log.debug("Tv Notify sent to: {}", channel.getChannelName());
    }

    @Override
    public void handleStatsNotifyRequest(NotifyRequest notifyRequest) {
        Channel channel = channelService.findOne(notifyRequest.getTargetChannelId());
        if (channel == null) {
            log.warn("Can't notify, no channel with id: {}", notifyRequest.getTargetChannelId());
            return;
        }
        HokanCore core = getConnectedEngine(channel.getNetwork());
        if (core == null) {
            log.warn("Can't notify, no HokanCore for channel: {}", channel);
            return;
        }
        core.handleSendMessage(channel.getChannelName(), notifyRequest.getNotifyMessage());
        log.debug("Stats Notify sent to: {}", channel.getChannelName());

    }

    @Override
    public void handleNotifyRequest(NotifyRequest notifyRequest) {
        Channel channel = channelService.findOne(notifyRequest.getTargetChannelId());
        if (channel == null) {
            log.warn("Can't notify, no channel with id: {}", notifyRequest.getTargetChannelId());
            return;
        }
        HokanCore core = getConnectedEngine(channel.getNetwork());
        if (core == null) {
            log.warn("Can't notify, no HokanCore for channel: {}", channel);
            return;
        }
        if (notifyRequest.getNotifyType().equals("TELEGRAM_NOTIFY_REQUEST")) {
            core.handleSendMessage(channel.getChannelName(), notifyRequest.getNotifyMessage(), true);
        } else {
            core.handleSendMessage(channel.getChannelName(), notifyRequest.getNotifyMessage(), false);
        }
        log.debug("NotifyRequest sent to: {}", channel.getChannelName());
    }


    public void startConnect() throws HokanException {

        updateServerMap();
        resetChannelStates();
        invalidateLoggedInUsers();

        for (IrcServerConfig server : this.configuredServers.values()) {
            log.debug(">> connecting server: " + server.getServer());
            if (server.getIrcServerConfigState() == IrcServerConfigState.CONNECTED) {
                try {
                    connect(server.getNetwork());
                } catch (HokanException e) {
                    log.error("Couldn't get engine online: " + server.getNetwork(), e);
                }
            }
        }
    }

    @Override
    public void run(String... args) throws Exception {
        log.debug("--> ");
        CommandLineArgsParser parser = new CommandLineArgsParser(args);
        Map<CommandLineArgs, String> parsed = parser.parseArgs();
        if (parsed.containsKey(CommandLineArgs.CONFIG_INIT)) {
            log.debug("Running configuration init!");
            configurationInit.initConfiguration();
            System.out.println("\n\nConfiguration done!\n\nNext run bot without --ConfigInit=true\n\n");
            SpringApplication.exit(context);

        } else {
            log.debug("START CONNECT!");
            startConnect();
        }
    }
}
