package org.freakz.hokan_ng_springboot.bot.io.telegram.service;

import org.freakz.hokan_ng_springboot.bot.common.events.EngineResponse;
import org.freakz.hokan_ng_springboot.bot.common.events.IrcEvent;
import org.freakz.hokan_ng_springboot.bot.common.events.IrcMessageEvent;
import org.freakz.hokan_ng_springboot.bot.common.events.MessageToTelegram;
import org.freakz.hokan_ng_springboot.bot.common.jms.api.JmsSender;
import org.freakz.hokan_ng_springboot.bot.common.jpa.entity.*;
import org.freakz.hokan_ng_springboot.bot.common.jpa.service.*;
import org.freakz.hokan_ng_springboot.bot.common.util.CommandLineArgsParser;
import org.freakz.hokan_ng_springboot.bot.common.util.StringStuff;
import org.freakz.hokan_ng_springboot.bot.io.telegram.jms.EngineCommunicator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.Date;
import java.util.List;

@Service
public class TelegramConnectService implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(TelegramConnectService.class);

    private static final String NETWORK_NAME = "telegramNetwork";

    private static final String CHANNEL_NAME = "telegramChannel";

    @Autowired
    private EngineCommunicator engineCommunicator;

    @Autowired
    private ChannelPropertyService channelPropertyService;

    @Autowired
    private ChannelService channelService;

    @Autowired
    private ChannelStatsService channelStatsService;

    @Autowired
    private NetworkService networkService;

    @Autowired
    private UserChannelService userChannelService;

    @Autowired
    private UserService userService;

    @Autowired
    private JmsSender jmsSender;

    private Network getNetwork() {
        Network network = networkService.getNetwork(NETWORK_NAME);
        if (network == null) {
            network = new Network(NETWORK_NAME);
        }
        return networkService.save(network);
    }

    private Channel getChannel(String channelName) {
        Channel channel;
        channel = channelService.findByNetworkAndChannelName(getNetwork(), channelName);

        if (channel == null) {
            channel = channelService.createChannel(getNetwork(), channelName);
        }
        channel.setChannelStartupState(ChannelStartupState.JOIN);
        channel.setChannelState(ChannelState.JOINED);
        return channelService.save(channel);
    }

    private Channel getChannel(IrcEvent ircEvent) {
        return getChannel(ircEvent.getChannel());
    }

    private ChannelStats getChannelStats(Channel channel) {
        ChannelStats channelStats = channelStatsService.findFirstByChannel(channel);
        if (channelStats == null) {
            channelStats = new ChannelStats();
            channelStats.setChannel(channel);
        }
        return channelStats;
    }

    private User getUser(IrcEvent ircEvent) {
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

    private User getUserByTelegramId(int telegramId) {
        User user = this.userService.getUserTelegramId(telegramId);
        return user;
    }

    public void sendMessageToEngine(String message, String sender, Long chatId, Long telegramUserId) {
//        IrcLog ircLog = this.ircLogService.addIrcLog(new Date(), sender, CHANNEL_NAME, message);

        Network nw = getNetwork();
        nw.addToLinesReceived(1);
        this.networkService.save(nw);

        IrcMessageEvent ircEvent;

        User botUser = getUserByTelegramId(telegramUserId.intValue());
        if (botUser != null) {
            ircEvent = new IrcMessageEvent("" + chatId, NETWORK_NAME, CHANNEL_NAME, botUser.getNick(), "telegramLogin", "telegramHost", message);
        } else {
            log.debug("Not linked User with telegramUserId: {}", telegramUserId);
            ircEvent = new IrcMessageEvent("" + chatId, NETWORK_NAME, CHANNEL_NAME, sender, "telegramLogin", "telegramHost", message);
        }


        User user = getUser(ircEvent);
        Channel ch = getChannel(ircEvent);
        ChannelStats channelStats = getChannelStats(ch);
        channelStats.addToLinesReceived(1);
        channelStatsService.save(channelStats);

        UserChannel userChannel = userChannelService.getUserChannel(user, ch);
        if (userChannel == null) {
            userChannel = new UserChannel(user, ch);
        }
        userChannel.setLastMessageTime(new Date());
        userChannelService.save(userChannel);

        if (message.startsWith("!")) {
            engineCommunicator.sendToEngine(ircEvent, null);
        } else {
            if (message.startsWith("=irclink")) {
                CommandLineArgsParser commandLineArgsParser;
            }
            if (message.equals("=myid")) {
                SendMessage idMessage = new SendMessage(); // Create a SendMessage object with mandatory fields
                idMessage.setChatId(chatId + "");
                idMessage.setText(sender + ": Your Telegram User ID: " + telegramUserId + "\n");
                try {
                    telegramBot.execute(idMessage);
//                    telegramBot.sendMessage(idMessage);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            } else if (!message.startsWith("=")) {
                List<Channel> channelsWithProperty = channelPropertyService.getChannelsWithProperty(PropertyName.PROP_CHANNEL_TELEGRAM_LINK, chatId + "");
                for (Channel channel : channelsWithProperty) {
                    long id = channel.getId();
                    log.debug("Sending to IRC channel, id: {}", id);
                    engineCommunicator.sendToIrcChannel(ircEvent, (int) id);
                }
            }
        }
    }

    private TelegramBot telegramBot;

    private void connectTelegram() throws TelegramApiException {
//        ApiContextInitializer.init();
        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
        try {
            telegramBot = new TelegramBot(this);
            botsApi.registerBot(telegramBot);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run(String... strings) throws Exception {
        connectTelegram();
    }

    public void handleEngineResponse(EngineResponse response) {
        final String botNick = response.getIrcMessageEvent().getBotNick();
        Long chatId = Long.valueOf(botNick);
        log.debug("Engine reply to: {}", botNick);
        SendMessage message = new SendMessage();
        message.setChatId(chatId + "");
        message.setText(response.getResponseMessage());
        try {
            telegramBot.execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void handleIrcMessage(IrcMessageEvent event) {
        long chatId = Long.valueOf(event.getParameter());
        log.debug("IRC event: {}", chatId);
        String fromIrc = String.format("%s: %s", event.getSender(), event.getMessage());
        SendMessage message = new SendMessage();
        message.setChatId(chatId + "");
        message.setText(fromIrc);
        try {
            telegramBot.execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void handleMessageToTelegram(MessageToTelegram message) {
        long chatId = Long.valueOf(message.getTelegramChatId());
        log.debug("MessageToTelegram: {}", chatId);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId + "");
        sendMessage.setText(message.getMessage());
        try {
            telegramBot.execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

    }
}
