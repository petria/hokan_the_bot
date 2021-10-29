package org.freakz.hokan_ng_springboot.bot.io.jms;

import org.freakz.hokan_ng_springboot.bot.common.enums.HokanModule;
import org.freakz.hokan_ng_springboot.bot.common.events.IrcMessageEvent;
import org.freakz.hokan_ng_springboot.bot.common.events.MessageToTelegram;
import org.freakz.hokan_ng_springboot.bot.common.events.ServiceRequest;
import org.freakz.hokan_ng_springboot.bot.common.events.ServiceRequestType;
import org.freakz.hokan_ng_springboot.bot.common.jms.api.JmsSender;
import org.freakz.hokan_ng_springboot.bot.common.jpa.entity.Alias;
import org.freakz.hokan_ng_springboot.bot.common.jpa.entity.UserChannel;
import org.freakz.hokan_ng_springboot.bot.common.jpa.service.AliasService;
import org.freakz.hokan_ng_springboot.bot.common.util.CommandArgs;
import org.freakz.hokan_ng_springboot.bot.common.util.StringStuff;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Petri Airio on 9.4.2015.
 * -
 */
@Service
public class CommunicatorImpl implements EngineCommunicator, ServiceCommunicator, TelegramCommunicator {

    private static final Logger log = LoggerFactory.getLogger(CommunicatorImpl.class);

    private final AliasService aliasService;

    private final JmsSender jmsSender;

    @Autowired
    public CommunicatorImpl(AliasService aliasService, JmsSender jmsSender) {
        this.aliasService = aliasService;
        this.jmsSender = jmsSender;
    }

    private boolean resolveAlias(IrcMessageEvent event) {
        String line = event.getMessage();
        List<Alias> aliases = aliasService.findAll();
        boolean wasAlias = false;
        for (Alias alias : aliases) {
            if (line.contains(alias.getAlias())) {
                String message = event.getMessage();
                String aliasMessage = message.replaceAll(alias.getAlias(), alias.getCommand());
                event.setMessage(aliasMessage);
                wasAlias = true;
            }
        }
        return wasAlias;
    }

    private boolean isLastCommandRepeatAlias(IrcMessageEvent event, UserChannel userChannel) {
        CommandArgs args = new CommandArgs(event.getMessage());
        if (args.getCmd().equals("!")) {
            String lastCommand = userChannel.getLastCommand();
            if (lastCommand != null && lastCommand.length() > 0) {
                CommandArgs lastCommandArgs = new CommandArgs(lastCommand);
                String aliasMessage;
                if (args.hasArgs()) {
                    String message = event.getMessage();
                    aliasMessage = message.replaceFirst("!", lastCommandArgs.getCmd());
                    event.setMessage(aliasMessage);
                } else {
                    String message = event.getMessage();
                    aliasMessage = message.replaceFirst("!", lastCommand);
                    event.setMessage(aliasMessage);
                }
//        event.setOutputPrefix(String.format("%s :: ", aliasMessage));
                return true;
            } else {
                log.debug("No valid lastCommand: {}", lastCommand);
                return false;
            }
        }
        return false;
    }

    @Override
    public String sendToEngine(IrcMessageEvent event, UserChannel userChannel) {
        if (event.getMessage().length() > 0) {
            try {
//                boolean repeatAlias = isLastCommandRepeatAlias(event, userChannel);
                boolean aliased = resolveAlias(event);
                String message = event.getMessage();
                boolean between = StringStuff.isInBetween(message, "&&", ' ');
                log.info("Aliased: {} - RepeatAlias: {} - between = {}", aliased, between);
                if (!message.startsWith("!alias") && between) {
                    String[] split = message.split("&&");
                    for (String splitted : split) {
                        IrcMessageEvent splitEvent = (IrcMessageEvent) event.clone();
                        String trimmed = splitted.trim();
                        splitEvent.setOutputPrefix(String.format("%s :: ", trimmed));
                        splitEvent.setMessage(trimmed);
                        if (trimmed.startsWith("!")) {
                            log.debug("Sending to engine: {}", trimmed);
                            jmsSender.send(HokanModule.HokanIo, HokanModule.HokanEngine.getQueueName(), "EVENT", splitEvent, false);
                        } else {
                            log.debug("Not a command: {}", trimmed);
                        }
                    }
                } else {
                    if (event.getMessage().startsWith("!")) {
                        log.debug("Sending to engine: {}", message);
                        jmsSender.send(HokanModule.HokanIo, HokanModule.HokanEngine.getQueueName(), "EVENT", event, false);
                    } else {
                        log.debug("Not a command: {}", message);
                    }
                }
            } catch (Exception e) {
                log.error("error", e);
            }
            return "Sent!";
        }
        return "not a command";
    }

    @Override
    public void sendServiceRequest(IrcMessageEvent ircEvent, ServiceRequestType requestType, Object... params) {
        ServiceRequest request = new ServiceRequest(requestType, ircEvent, new CommandArgs(ircEvent.getMessage()), params);
        try {
            jmsSender.send(HokanModule.HokanIo, HokanModule.HokanServices.getQueueName(), "SERVICE_REQUEST", request, false);
        } catch (Exception e) {
            log.error("error", e);
        }

    }


    @Override
    public void sendIrcMessageEventToTelegram(IrcMessageEvent ircEvent) {
        try {
            log.debug("Sending to telegram!");
            jmsSender.send(HokanModule.HokanIo, HokanModule.HokanIoTelegram.getQueueName(), "IRC_MESSAGE", ircEvent, false);
        } catch (Exception e) {
            log.error("error", e);
        }
    }

    @Override
    public void sendMessageToTelegram(String message, String telegramChatId) {
        MessageToTelegram messageToTelegram = new MessageToTelegram(message, telegramChatId);
        try {
            log.debug("Sending to telegram!");
            jmsSender.send(HokanModule.HokanIo, HokanModule.HokanIoTelegram.getQueueName(), "MESSAGE_TO_TELEGRAM", messageToTelegram, false);
        } catch (Exception e) {
            log.error("error", e);
        }

    }
}
