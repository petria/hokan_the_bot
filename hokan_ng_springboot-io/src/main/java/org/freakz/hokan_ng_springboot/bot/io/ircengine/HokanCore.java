package org.freakz.hokan_ng_springboot.bot.io.ircengine;

import org.freakz.hokan_ng_springboot.bot.common.core.HokanCoreService;
import org.freakz.hokan_ng_springboot.bot.common.events.*;
import org.freakz.hokan_ng_springboot.bot.common.exception.HokanException;
import org.freakz.hokan_ng_springboot.bot.common.jpa.entity.*;
import org.freakz.hokan_ng_springboot.bot.common.jpa.service.*;
import org.freakz.hokan_ng_springboot.bot.common.service.AccessControlService;
import org.freakz.hokan_ng_springboot.bot.common.util.CommandArgs;
import org.freakz.hokan_ng_springboot.bot.common.util.IRCUtility;
import org.freakz.hokan_ng_springboot.bot.common.util.StringStuff;
import org.freakz.hokan_ng_springboot.bot.io.ircengine.connector.EngineConnector;
import org.freakz.hokan_ng_springboot.bot.io.jms.EngineCommunicator;
import org.freakz.hokan_ng_springboot.bot.io.jms.ServiceCommunicator;
import org.freakz.hokan_ng_springboot.bot.io.jms.TelegramCommunicator;
import org.jibble.pircbot.PircBot;
import org.jibble.pircbot.PircBotUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.freakz.hokan_ng_springboot.bot.common.jpa.entity.PropertyName.PROP_SYS_EXT_TITLE_RESOLVER;

/**
 * Created by AirioP on 17.2.2015.
 * -
 */
@Component
@Scope("prototype")
public class HokanCore extends PircBot implements HokanCoreService {

    private static final Logger log = LoggerFactory.getLogger(HokanCore.class);

    @Autowired
    private AccessControlService accessControlService;

    @Autowired
    private ChannelService channelService;

    @Autowired
    private ChannelStatsService channelStatsService;

    @Autowired
    private ChannelPropertyService channelPropertyService;

    @Autowired
    private EngineCommunicator engineCommunicator;

    @Autowired
    private JoinedUserService joinedUsersService;

    @Autowired
    private NetworkService networkService;

    @Autowired
    private PropertyService propertyService;

    @Autowired
    private UserChannelService userChannelService;

    @Autowired
    private ServiceCommunicator serviceCommunicator;

    @Autowired
    private SearchReplaceService searchReplaceService;

    @Autowired
    private TelegramCommunicator telegramCommunicator;

    @Autowired
    private UserService userService;

    @Autowired
    private OutputQueue outputQueue;

    private EngineConnector engineConnector;

    private IrcServerConfig ircServerConfig;

    private Map<String, String> serverProperties = new HashMap<>();

    private Map<String, List<String>> whoQueries = new HashMap<>();

    private Map<String, ConfirmResponse> confirmResponseMap = new HashMap<>();

    public HokanCore() {
    }

    public void init(String botName, IrcServerConfig ircServerConfig) {
        this.ircServerConfig = ircServerConfig;
        setVerbose(true);
        setName(botName);
        setVersion("Hokan NG");
        setLogin("hokan");
        setMessageDelay(1100);
    }

    public IrcServerConfig getIrcServerConfig() {
        return this.ircServerConfig;
    }

    public void setIrcServerConfig(IrcServerConfig ircServerConfig) {
        this.ircServerConfig = ircServerConfig;
    }

    public void startOutputQueue() {
        this.outputQueue.init(this, getIrcServerConfig().isThrottleInUse());
    }

    @Override
    public void dispose() {
        outputQueue.stop();
        List<Runnable> runnableList = executor.shutdownNow();
        log.info("Runnable list  size: {}", runnableList.size());
        super.dispose();
    }

    @Autowired
    public void setEngineConnector(EngineConnector engineConnector) {
        this.engineConnector = engineConnector;
    }

    public void log(String message) {
        if (!message.contains("PING") && !message.contains("PONG")) {
            log.info(message);
        }
    }

    @Override
    protected void onUnknown(String line) {
        log.info("UNKNOWN: {}", line);
        if (line.contains("Ping timeout")) {
            this.engineConnector.engineConnectorPingTimeout(this);
        } else if (line.toLowerCase().contains("excess flood")) {
            this.engineConnector.engineConnectorExcessFlood(this);
        }
    }

    public void sendWhoQuery(String channel) {
        log.info("Sending WHO query to: " + channel);
        List<String> whoReplies = new ArrayList<>();
        whoQueries.put(channel.toLowerCase(), whoReplies);
        sendRawLineViaQueue("WHO " + channel);
    }

    @Override
    protected void onUserList(String channel, PircBotUser[] pircBotUsers) {
        sendWhoQuery(channel);
    }

    public Network getNetwork() {
        return networkService.getNetwork(getIrcServerConfig().getNetwork().getName());
    }

    public Channel getChannel(String channelName) {
        Channel channel;
        channel = channelService.findByNetworkAndChannelName(getNetwork(), channelName);

        if (channel == null) {
            channel = channelService.createChannel(getNetwork(), channelName);
        }
        return channel;
    }

    public Channel getChannel(IrcEvent ircEvent) {
        return getChannel(ircEvent.getChannel());
    }

    public ChannelStats getChannelStats(Channel channel) {
        ChannelStats channelStats = channelStatsService.findFirstByChannel(channel);
        if (channelStats == null) {
            channelStats = new ChannelStats();
            channelStats.setChannel(channel);
        }
        return channelStats;
    }

    public UserChannel getUserChannel(User user, Channel channel) {
        UserChannel userChannel = userChannelService.getUserChannel(user, channel);
        if (userChannel == null) {
            userChannel = userChannelService.createUserChannel(user, channel);
        }
        return userChannel;
    }

    public User getUser(IrcEvent ircEvent) {
        User user;
        User maskUser = this.userService.getUserByMask(ircEvent.getMask());
        if (maskUser != null) {
            user = maskUser;
        } else {
            user = this.userService.findFirstByNick(ircEvent.getSender());
            if (user == null) {
                user = new User(ircEvent.getSender());
                user = userService.save(user);
            }
        }
        user.setRealMask(StringStuff.quoteRegExp(ircEvent.getMask()));
        this.userService.save(user);
        return user;
    }

    private void handleWhoList(String channelName, List<String> whoReplies) throws HokanException {
        Channel channel = getChannel(channelName);
        ChannelStats channelStats = getChannelStats(channel);
        if (whoReplies.size() > channelStats.getMaxUserCount()) {
            channelStats.setMaxUserCount(whoReplies.size());
            channelStats.setMaxUserCountDate(new Date());
        }
        channelStats.setLastActive(new Date());
        channelStatsService.save(channelStats);

        this.joinedUsersService.clearJoinedUsers(channel);
        for (String whoLine : whoReplies) {
            String[] split = whoLine.split(" ");
            String nick = split[5];
            String mask = split[5] + "!" + split[2] + "@" + split[3];
            String userModes = split[6];
            String fullName = StringStuff.joinStringArray(split, 8);
            User user = this.userService.findFirstByNick(nick);
            if (user == null) {
                user = new User();
                user.setNick(split[5]);
                user.setMask(StringStuff.quoteRegExp(mask));
                user.setPassword("not_set");
                user.setFullName(fullName);
            }
            user.setRealMask(StringStuff.quoteRegExp(mask));
            user = this.userService.save(user);

//      getUserChannel(user, channel);
/*      UserChannel userChannel = userChannelService.getUserChannel(user, channel);
      if (userChannel == null) {
        userChannelService.createUserChannel(user, channel);
      }*/
            this.joinedUsersService.createJoinedUser(channel, user, userModes);
        }
    }

    @Override
    protected void onTopic(String channelName, String topic, String setBy, long date, boolean changed) {
        IrcMessageEvent ircEvent = (IrcMessageEvent) IrcEventFactory.createIrcMessageEvent(getName(), getNetwork().getName(), channelName, setBy, "topic", "topic", topic);
        ircEvent.setTimestamp(date);
        ChannelStats channelStats = getChannelStats(getChannel(channelName));
        channelStats.setTopicSetBy(setBy);
        channelStats.setTopicSetDate(new Date());
        channelStatsService.save(channelStats);
        serviceCommunicator.sendServiceRequest(ircEvent, ServiceRequestType.CHANNEL_TOPIC_SET_REQUEST);
        log.info("Topic '{}' set by {}", topic, channelStats.getTopicSetBy());
    }

    @Override
    protected void onNickChange(String oldNick, String login, String hostname, String newNick) {
        log.debug("Nick changed, refreshing channels with who query!");
        for (String channel : getChannels()) {
            sendWhoQuery(channel);
        }
    }

    @Override
    protected void onKick(String channelName, String kickerNick, String kickerLogin, String kickerHostname, String recipientNick, String reason) {
        log.info("{} kicked from {}", recipientNick, channelName);
        sendWhoQuery(channelName);
        IrcEvent ircEvent = IrcEventFactory.createIrcEvent(getName(), getNetwork().getName(), channelName, kickerNick, kickerLogin, kickerHostname);
        if (recipientNick.equalsIgnoreCase(getNick())) {
            Channel channel = getChannel(ircEvent);
            channel.setChannelState(ChannelState.KICKED_OUT);
            channelService.save(channel);
        }
    }

    @Override
    protected void onPart(String channelName, String sender, String login, String hostname, String message) {
        log.info("{} parted channel: {}", sender, channelName);
        sendWhoQuery(channelName);

        IrcEvent ircEvent = IrcEventFactory.createIrcEvent(getName(), getNetwork().getName(), channelName, sender, login, hostname);

        if (sender.equalsIgnoreCase(getNick())) {
            Channel channel = getChannel(ircEvent);
            channel.setChannelState(ChannelState.PARTED);
            channelService.save(channel);
        }
    }

    @Override
    protected void onJoin(String channelName, String sender, String login, String hostname) {
        log.info("{} joined channel: {}", sender, channelName);
        sendTopicQuery(channelName);
        IrcEvent ircEvent = IrcEventFactory.createIrcEvent(getName(), getNetwork().getName(), channelName, sender, login, hostname);
        Channel channel = getChannel(ircEvent);
        ChannelStats channelStats = getChannelStats(channel);
        User user = getUser(ircEvent);
//    UserChannel userChannel = getUserChannel(user, channel);

        if (sender.equalsIgnoreCase(getNick())) {
            // Bot joining
            Network nw = getNetwork();
            nw.addToChannelsJoined(1);
            this.networkService.save(nw);
            channel.setChannelState(ChannelState.JOINED);
            if (channelStats.getFirstJoined() == null) {
                Date d = new Date();
                channelStats.setLastWriter(getName());
                channelStats.setMaxUserCount(1);
                channelStats.setFirstJoined(d);
                channelStats.setLastActive(d);
                channelStats.setMaxUserCountDate(d);
                channelStats.setWriterSpreeOwner(getName());
            }

        } else {
            boolean doJoin = channelPropertyService.getChannelPropertyAsBoolean(channel, PropertyName.PROP_CHANNEL_DO_JOIN_MESSAGE, false);
/* TODO
       String message = userChannel.getJoinComment();
        if (message != null && message.length() > 0) {
          handleSendMessage(channel.getChannelName(), String.format("%s -> %s", sender, message));
        }
      }*/
        }
        int oldC = channelStats.getMaxUserCount();
        int newC = getUsers(channel.getChannelName()).length;
        if (newC > oldC) {
            log.info("Got new channel users record: " + newC + " > " + oldC);
            channelStats.setMaxUserCount(newC);
            channelStats.setMaxUserCountDate(new Date());
        }

        channelService.save(channel);
        channelStatsService.save(channelStats);
    }

    private void sendTopicQuery(String channelName) {
        sendRawLineViaQueue("TOPIC " + channelName);
    }

    @Override
    protected void onOp(String channel, String sourceNick, String sourceLogin, String sourceHostname, String recipient) {
        sendWhoQuery(channel);
    }

    @Override
    protected void onDeop(String channel, String sourceNick, String sourceLogin, String sourceHostname, String recipient) {
        sendWhoQuery(channel);
    }

    @Override
    protected void onVoice(String channel, String sourceNick, String sourceLogin, String sourceHostname, String recipient) {
        sendWhoQuery(channel);
    }

    @Override
    protected void onDeVoice(String channel, String sourceNick, String sourceLogin, String sourceHostname, String recipient) {
        sendWhoQuery(channel);
    }

    @Override
    protected void onServerResponse(int code, String line) {
        if (code == RPL_WHOREPLY) {
            String[] split = line.split(" ");
            if (split.length >= 6) {
                String channel = split[1];
                List<String> whoReplies = whoQueries.get(channel.toLowerCase());
                if (whoReplies == null) {
                    log.debug("was null?? --> {}", whoQueries);
                    whoReplies = new ArrayList<>();
                    whoQueries.put(channel.toLowerCase(), whoReplies);
                }
                whoReplies.add(line);

            } else {
                log.info("SKIPPED WHO REPLY: {}", line);
            }

        } else if (code == RPL_ENDOFWHO) {
            String[] split = line.split(" ");
            String channel = split[1];
            List<String> whoReplies = this.whoQueries.remove(channel.toLowerCase());
            try {
                handleWhoList(channel, whoReplies);
            } catch (HokanException e) {
                log.error("Core error", e);
            }
            log.info("Handled {} WHO lines!", whoReplies.size());

        }
        if (code == 5) {
            String[] split = line.split(" ");
            for (String str : split) {
                if (str.contains("=")) {
                    String[] keyValue = str.split("=");
                    this.serverProperties.put(keyValue[0], keyValue[1]);
                    log.info("--> {}: {}", keyValue[0], keyValue[1]);
                }
            }
        }
    }

    private boolean isBotOp(Channel channel) {
        for (JoinedUser user : joinedUsersService.findJoinedUsers(channel)) {
            if (user.getUser().getNick().equalsIgnoreCase(getName())) {
                return user.isOp();
            }
        }
        return false;
    }

    @Override
    protected void onDisconnect() {
        engineConnector.engineConnectorDisconnected(this);
    }

    @Override
    protected void onPrivateMessage(String sender, String login, String hostname, String message, byte[] original) {
        int confirmLong = propertyService.getPropertyAsInt(PropertyName.PROP_SYS_CONFIRM_LONG_MESSAGES, -1);
        if (confirmLong > 0) {
            if (handleConfirmMessages(sender, message)) {
                log.info("Confirm message handled!");
                return;
            }
        }

        IrcMessageEvent ircEvent = (IrcMessageEvent) IrcEventFactory.createIrcMessageEvent(getName(), getNetwork().getName(), "@privmsg", sender, login, hostname, message);
        ircEvent.setOriginal(original);
        ircEvent.setPrivate(true);

        sendIrcChannelLogRequest(ircEvent);

        Network nw = getNetwork();
        nw.addToLinesReceived(1);
        this.networkService.save(nw);

        User user = getUser(ircEvent);

        if (accessControlService.isAdminUser(user)) {
            handleBuiltInCommands(ircEvent);
        }
        PropertyEntity adminToken = propertyService.findFirstByPropertyName(PropertyName.PROP_SYS_ADMIN_USER_TOKEN);
        if (adminToken != null) {
            handleAdminUserToken(ircEvent, user, adminToken);
        }

        boolean ignore = accessControlService.hasUserFlag(user, UserFlag.IGNORE_ON_CHANNEL);
        if (ignore) {
            log.debug("Ignoring: {}", user);
        } else {
            catchUrlsWithTitleResolver(ircEvent);
            Channel channel = getChannel(ircEvent);
            UserChannel userChannel = getUserChannel(user, channel);
            engineCommunicator.sendToEngine(ircEvent, userChannel);
        }

        String urlTitleResolverNick = propertyService.getPropertyAsString(PROP_SYS_EXT_TITLE_RESOLVER, null);
        if (urlTitleResolverNick != null && !urlTitleResolverNick.isEmpty()) {
            if (sender.toLowerCase().equalsIgnoreCase(urlTitleResolverNick)) {
                handleTitleResolverReply(urlTitleResolverNick, ircEvent);
            } else {
                serviceCommunicator.sendServiceRequest(ircEvent, ServiceRequestType.CATCH_URLS_REQUEST);
            }
        }
    }

    private void catchUrlsWithTitleResolver(IrcMessageEvent ircEvent) {
        String urlTitleResolverNick = propertyService.getPropertyAsString(PROP_SYS_EXT_TITLE_RESOLVER, null);
        log.debug("urlTitleResolverNick: {}", urlTitleResolverNick);
        if (urlTitleResolverNick != null && !urlTitleResolverNick.isEmpty()) {
            String msg = ircEvent.getMessage();
            String regexp = "(https?://|www\\.)\\S+";

            Pattern p = Pattern.compile(regexp, Pattern.CASE_INSENSITIVE);
            Matcher m = p.matcher(msg);
            while (m.find()) {
                String url = m.group();
                sendToResolverNick(ircEvent, url);
            }
        } else {
            serviceCommunicator.sendServiceRequest(ircEvent, ServiceRequestType.CATCH_URLS_REQUEST);
        }

    }

    private AtomicInteger sentCount = new AtomicInteger(0);
    private AtomicInteger receiveCount = new AtomicInteger(1);
    private Map<Integer, IrcMessageEvent> sentCountMap = new ConcurrentHashMap<>();


    private void handleTitleResolverReply(String urlTitleResolverNick, IrcMessageEvent ircEvent) {
        Integer count = receiveCount.get();
        IrcMessageEvent orgEvent = sentCountMap.get(count);

        log.debug("Reply from {} -> count: {} -> orig event: {}", urlTitleResolverNick, count, orgEvent);

        if (orgEvent == null) {
            log.debug("No matching event: {}", count);
        } else {
            orgEvent = sentCountMap.remove(count);
            receiveCount.addAndGet(1);
            log.debug("Resolver message: {}", ircEvent.getMessage());
            orgEvent.setParameter(ircEvent.getMessage());
            UrlCatchResolvedEvent event = UrlCatchResolvedEvent.builder().url(orgEvent.getMessage()).title(ircEvent.getMessage()).build();

            ircEvent.setChannel(orgEvent.getChannel());
            serviceCommunicator.sendServiceRequest(ircEvent, ServiceRequestType.CATCH_URLS_REQUEST, event);
        }

    }

    private void sendToResolverNick(IrcMessageEvent ircEvent, String url) {
        Integer count = sentCount.addAndGet(1);
        ircEvent.setMessage(url);
        ircEvent.setParameter(url);
        sentCountMap.put(count, ircEvent);
        String urlTitleResolverNick = propertyService.getPropertyAsString(PROP_SYS_EXT_TITLE_RESOLVER, null);
        if (urlTitleResolverNick != null && !urlTitleResolverNick.isEmpty()) {
            log.debug("Sending url to {} to resolve, count: {} ", urlTitleResolverNick, count);
            sendMessage(urlTitleResolverNick, url);
        }
    }

    @Override
    protected void onMessage(String channel, String sender, String login, String hostname, String message,
                             byte[] original) {

        String toMe = String.format("%s: ", getName());
        boolean isToMe = false;
        if (message.startsWith(toMe)) {
            message = message.replaceFirst(toMe, "");
            isToMe = true;
        }

        Network nw = getNetwork();
        nw.addToLinesReceived(1);
        this.networkService.save(nw);

        IrcMessageEvent ircEvent = (IrcMessageEvent) IrcEventFactory.createIrcMessageEvent(getName(), nw.getName(), channel, sender, login, hostname, message);
        ircEvent.setOriginal(original);
        ircEvent.setToMe(isToMe);
        sendIrcChannelLogRequest(ircEvent);

        User user = getUser(ircEvent);
        Channel ch = getChannel(ircEvent);
        ircEvent.setBotOp(isBotOp(ch));
        ircEvent.setChannelId(ch.getId());

        ChannelStats channelStats = channelStatsService.findFirstByChannel(ch);
        if (channelStats == null) {
            channelStats = new ChannelStats();
            channelStats.setChannel(ch);
        }


        String lastWriter = channelStats.getLastWriter();
        if (lastWriter != null && lastWriter.equalsIgnoreCase(sender)) {
            int spree = channelStats.getLastWriterSpree();
            spree++;
            channelStats.setLastWriterSpree(spree);
            if (spree > channelStats.getWriterSpreeRecord()) {
                channelStats.setWriterSpreeRecord(spree);
                channelStats.setWriterSpreeOwner(sender);
            }
        } else {
            channelStats.setLastWriterSpree(1);
        }

        channelStats.setLastActive(new Date());
        channelStats.setLastWriter(ircEvent.getSender());
        channelStats.addToLinesReceived(1);

        channelStatsService.save(channelStats);

        UserChannel userChannel = userChannelService.getUserChannel(user, ch);
        if (userChannel == null) {
            userChannel = new UserChannel(user, ch);
        }

        userChannel.setLastMessageTime(new Date());
        userChannelService.save(userChannel);

        boolean wlt = channelPropertyService.getChannelPropertyAsBoolean(ch, PropertyName.PROP_CHANNEL_DO_WHOLELINE_TRICKERS, false);
        if (wlt || ircEvent.isToMe()) {
            sendWholeLineTriggerRequest(ircEvent);
        }

        if (accessControlService.isAdminUser(user)) {
            handleBuiltInCommands(ircEvent);
        }
        this.channelService.save(ch);

        String telegram = channelPropertyService.getChannelPropertyAsString(ch, PropertyName.PROP_CHANNEL_TELEGRAM_LINK, null);
        if (telegram != null && !telegram.equals("n/a")) {
            ircEvent.setParameter(telegram);
            telegramCommunicator.sendIrcMessageEventToTelegram(ircEvent);
        }

        boolean ignore = accessControlService.hasUserFlag(user, UserFlag.IGNORE_ON_CHANNEL);
        if (ignore) {
            log.debug("Ignoring: {}", user);
        } else {
            catchUrlsWithTitleResolver(ircEvent);
//
            String result = engineCommunicator.sendToEngine(ircEvent, userChannel);
        }

        serviceCommunicator.sendServiceRequest(ircEvent, ServiceRequestType.TOP_COUNT_REQUEST);
    }

    private void sendWholeLineTriggerRequest(IrcMessageEvent ircEvent) {
        log.debug("ServiceRequestType.WHOLE_LINE_TRIGGER");
        serviceCommunicator.sendServiceRequest(ircEvent, ServiceRequestType.WHOLE_LINE_TRIGGER);
    }

    private void sendIrcChannelLogRequest(IrcMessageEvent ircEvent) {
        log.debug("ServiceRequestType.IRC_CHANNEL_LOG_REQUEST");
        serviceCommunicator.sendServiceRequest(ircEvent, ServiceRequestType.IRC_CHANNEL_LOG_REQUEST);
    }

    private void handleAdminUserToken(IrcMessageEvent ircEvent, User user, PropertyEntity token) {
        CommandArgs args = new CommandArgs(ircEvent.getMessage());
        if (args.getCmd().equalsIgnoreCase("@AdminUserToken")) {
            String userToken = args.getArg(1);
            String tokenValue = token.getValue();
            if (userToken.equals(tokenValue)) {
                Set<UserFlag> flags = new HashSet<>();
                flags.add(UserFlag.ADMIN);
                user = accessControlService.addUserFlags(user, flags);
                log.debug("AdminUserToken granted for: {}", user.getNick());
                handleSendMessage(ircEvent.getSender(), "You are now Admin!");
                propertyService.delete(token);
            }
        }
    }

    private void handleBuiltInCommands(IrcMessageEvent ircEvent) {
        String message = ircEvent.getMessage();
        CommandArgs args = new CommandArgs(ircEvent.getMessage());
        if (message.startsWith("@qset ")) {

            boolean ok = outputQueue.setQueueValues(args.getArgs());
            handleSendMessage(ircEvent.getChannel(), "@qset: " + ok);
            String info = String.format("Throttle[%s]: sleepTime %d - maxLines - %d - fullLineLength %d - fullLineSleepTime %d - throttleBaseSleepTime %d",
                    outputQueue.isUsingThrottle() + "",
                    outputQueue.defSleepTime, outputQueue.defMaxLines,
                    outputQueue.defFullLineLength, outputQueue.defFullLineSleepTime,
                    outputQueue.defThrottleBaseSleepTime);

            handleSendMessage(ircEvent.getChannel(), info);
        } else if (message.equals("@clist")) {
            if (confirmResponseMap.size() > 0) {
                String cList = "";
                for (ConfirmResponse confirmResponse : confirmResponseMap.values()) {
                    cList += "  " + confirmResponse.toString();
                }
                handleSendMessage(ircEvent.getChannel(), "Confirmation list: " + cList);
            } else {
                handleSendMessage(ircEvent.getChannel(), "Confirmation list is empty!");
            }
        } else if (message.equals("@cclear")) {

            confirmResponseMap.clear();
            handleSendMessage(ircEvent.getChannel(), "Confirmation list cleared!");

        } else if (message.equals("@qclear")) {

            outputQueue.clearOutQueue();
            handleSendMessage(ircEvent.getChannel(), "OutQueue cleared!");

        } else if (message.startsWith("@qthrottle")) {

            outputQueue.setThrottle(StringStuff.parseBooleanString(args.getArgs()));
            handleSendMessage(ircEvent.getChannel(), String.format("Throttle[%s]", outputQueue.isUsingThrottle() + ""));

        } else if (message.equals("@qinfo")) {
            String info = String.format("Throttle[%s]: sleepTime %d - maxLines - %d - fullLineLength %d - fullLineSleepTime %d - throttleBaseSleepTime %d",
                    outputQueue.isUsingThrottle() + "",
                    outputQueue.defSleepTime, outputQueue.defMaxLines,
                    outputQueue.defFullLineLength, outputQueue.defFullLineSleepTime,
                    outputQueue.defThrottleBaseSleepTime);

            handleSendMessage(ircEvent.getChannel(), info);

        } else if (message.equals("@methodmap")) {
            log.info("Re-building method map!");
            buildMethodMap();
        }
    }

    private Map<String, Method> methodMap = null;

    private void buildMethodMap() {
        Class clazz = HokanCore.class;
        Method[] methods = clazz.getMethods();
        this.methodMap = new HashMap<>();
        for (Method method : methods) {
            methodMap.put(method.getName(), method);
        }
        log.info("Built method map, size {}", methodMap.size());

    }

    private Method getEngineMethod(String name) {
        if (this.methodMap == null) {
            buildMethodMap();
        }
        List<Method> matches = new ArrayList<>();
        for (Method method : methodMap.values()) {
            if (method.getName().equals(name)) { // && method.getGenericParameterTypes().length == args) {
                matches.add(method);
            }
        }
        if (matches.size() == 1) {
            return matches.get(0);
        } else if (matches.size() > 1) {
            log.info("ffufu"); // TODO
            return matches.get(0);
        }
        return null;
    }

    private boolean handleConfirmMessages(String sender, String message) {
        ConfirmResponse confirmResponse = confirmResponseMap.get(message.trim());
        if (confirmResponse != null) {
            doHandleEngineResponse(confirmResponse.getResponse());
            confirmResponseMap.remove(confirmResponse);
            return true;
        }
        return false;
    }

    private void doHandleEngineResponse(EngineResponse response) {

        if (response.getException() != null) {
            String error = " failed: " + response.getException().getMessage();
            String message = response.getIrcMessageEvent().getSender() + ": " + error;
            String target = response.getIrcMessageEvent().getChannel();
            sendMessage(target, message);
            return;
        }
        handleSendMessage(response);
        for (EngineMethodCall methodCall : response.getEngineMethodCalls()) {

            String methodName = methodCall.getMethodName();
            String[] methodArgs = methodCall.getMethodArgs();

            log.info("Executing engine method : " + methodName);
            log.info("Engine method args      : " + StringStuff.arrayToString(methodArgs, ", "));
            Method method = getEngineMethod(methodName);
            if (method != null) {
                String[] args = new String[method.getParameterTypes().length];
                int i = 0;
                for (String arg : methodArgs) {
                    args[i] = arg;
                    i++;
                }
                log.info("Using method args       : " + StringStuff.arrayToString(args, ", "));
                try {
                    log.info("Invoking method         : {}", method);
                    Object result = method.invoke(this, (Object[]) args);
                    log.info("Invoke   result         : {}", result);
                } catch (Exception e) {
                    log.error("Couldn't do engine method!", e);
                }
            } else {
                log.error("Couldn't find method for: " + methodName);
            }
        }

    }

    // @Override
    @SuppressWarnings({"ThrowableResultOfMethodCallIgnored"})
    public void handleEngineResponse(EngineResponse response) {
//    log.debug("Handle: {}", response);
        int confirmLong = propertyService.getPropertyAsInt(PropertyName.PROP_SYS_CONFIRM_LONG_MESSAGES, -1);
        if (confirmLong > 0) {
            int lines = response.getResponseMessage().split("\n").length;
            if (lines > confirmLong) {
                ConfirmResponse confirmResponse = new ConfirmResponse(response);
                String confirmKey = String.format("C%d", propertyService.getNextPid());
                confirmResponseMap.put(confirmKey, confirmResponse);
                String message = String.format("Your command caused too much output: %d / max %d - ", lines, confirmLong);
                message += String.format("To send it confirm with: %s", confirmKey);
                String raw = "PRIVMSG " + response.getIrcMessageEvent().getSender() + " :" + message;
                this.outputQueue.addLine(raw);
                return;
            }
        }
        doHandleEngineResponse(response);
        Channel channel = getChannel(response.getIrcMessageEvent());
        ChannelStats channelStats = getChannelStats(channel);
        channelStats.addToCommandsHandled(1);
        channelStatsService.save(channelStats);
    }

    protected void handleSendMessage(EngineResponse response) {
        String channel = response.getReplyTo();
        String message = response.getResponseMessage();
        if (message != null && message.trim().length() > 1) {
            boolean doSr = false;
            if (!response.isNoSearchReplace()) {
                if (!response.getIrcMessageEvent().isPrivate()) {
                    Channel ch = getChannel(response.getIrcMessageEvent().getChannel());
                    doSr = channelPropertyService.getChannelPropertyAsBoolean(ch, PropertyName.PROP_CHANNEL_DO_SEARCH_REPLACE, false);
                }
            }
            handleSendMessage(channel, message, doSr,
                    response.getIrcMessageEvent().getOutputPrefix(), response.getIrcMessageEvent().getOutputPostfix());
        }
    }

    private String handleSearchReplace(String message) {
        List<SearchReplace> searchReplaces = searchReplaceService.findAll();
        for (SearchReplace sr : searchReplaces) {
            try {
                message = Pattern.compile(sr.getSearch(), Pattern.CASE_INSENSITIVE).matcher(message).replaceAll(sr.getReplace());
            } catch (Exception e) {
                message += " || SR error: " + sr.getId() + " || ";
                break;
            }
        }
        return message;
    }

    private boolean hasChannelSearchReplace(String channelName) {
        final Channel channel = getChannel(channelName);
        if (channel != null) {
            return channelPropertyService.getChannelPropertyAsBoolean(channel, PropertyName.PROP_CHANNEL_DO_SEARCH_REPLACE, false);
        }
        return false;
    }

    public void handleSendMessage(String channel, String message, boolean fromTelegram) {
        handleSendMessage(channel, message, hasChannelSearchReplace(channel), null, null, true);
    }

    public void handleSendMessage(String channel, String message) {
        handleSendMessage(channel, message, hasChannelSearchReplace(channel), null, null);
    }

    public void handleSendMessage(String channel, String message, boolean doSr, String prefix, String postfix) {
        handleSendMessage(channel, message, doSr, prefix, postfix, false);
    }

    public void handleSendMessage(String channel, String message, boolean doSr, String prefix, String postfix, boolean fromTelegram) {

        if (doSr) {
            message = handleSearchReplace(message);
        }

        Channel ch = null;
        if (channel.startsWith("#")) {
            ch = getChannel(channel);
        }
        if (prefix == null) {
            prefix = "";
        }
        if (postfix == null) {
            postfix = "";
        }
        boolean bbMode = channelPropertyService.getChannelPropertyAsBoolean(ch, PropertyName.PROP_CHANNEL_BB_MODE, false);
        if (bbMode) {
            int rnd = 1 + (int) (Math.random() * 100);
            if (rnd > 75) {
                prefix = "yo, " + prefix;
            }
            int rnd2 = 1 + (int) (Math.random() * 100);
            if (rnd2 > 75) {
                postfix = postfix + ", bitch";
            }
        }

        Network nw = getNetwork();
        ChannelStats stats = getChannelStats(ch);

        String telegramId = channelPropertyService.getChannelPropertyAsString(ch, PropertyName.PROP_CHANNEL_TELEGRAM_LINK, null);
        boolean sendTelegram = false;
        if (!fromTelegram) {
            if (telegramId != null) {
                sendTelegram = true;
            }
        }

        String[] lines = message.split("\n");
        for (String line : lines) {
            if (sendTelegram) {
                String toTelegram = String.format("%s@%s %s", getNick(), channel, line);
                this.telegramCommunicator.sendMessageToTelegram(toTelegram, telegramId);
            }
            String[] split = IRCUtility.breakUpMessageByIRCLineLength(channel, line);
            for (String l : split) {
                String msg = prefix + l + postfix;
                String raw = "PRIVMSG " + channel + " :" + msg;
                this.outputQueue.addLine(raw);

                if (ch != null) {
                    stats.addToLinesSent(1);
                    IrcMessageEvent ircMessageEvent = new IrcMessageEvent();
                    ircMessageEvent.setSender(getNick());
                    ircMessageEvent.setChannel(ch.getChannelName());
                    ircMessageEvent.setNetwork(nw.getName());
                    ircMessageEvent.setMessage(msg);
                    try {
                        sendIrcChannelLogRequest(ircMessageEvent);
                    } catch (Exception e) {
                        log.error("logging error!", e);
                    }
                }
                nw.addToLinesSent(1);
            }
        }
        if (stats != null && ch != null) {
            this.channelStatsService.save(stats);
        }
        this.networkService.save(nw);
    }

}


