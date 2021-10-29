package org.freakz.hokan_ng_springboot.bot.io.telegram.jms;

import org.freakz.hokan_ng_springboot.bot.common.enums.HokanModule;
import org.freakz.hokan_ng_springboot.bot.common.events.IrcMessageEvent;
import org.freakz.hokan_ng_springboot.bot.common.events.NotifyRequest;
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
public class CommunicatorImpl implements EngineCommunicator, ServiceCommunicator {

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

    @Override
    public String sendToEngine(IrcMessageEvent event, UserChannel userChannel) {
        if (event.getMessage().length() > 0) {
            try {
                boolean repeatAlias = false; //isLastCommandRepeatAlias(event, userChannel);
                boolean aliased = resolveAlias(event);
                String message = event.getMessage();
                boolean between = StringStuff.isInBetween(message, "&&", ' ');
                log.info("Aliased: {} - RepeatAlias: {} - between = {}", aliased, repeatAlias, between);
                if (!message.startsWith("!alias") && between) {
                    String[] split = message.split("&&");
                    for (String splitted : split) {
                        IrcMessageEvent splitEvent = (IrcMessageEvent) event.clone();
                        String trimmed = splitted.trim();
                        splitEvent.setOutputPrefix(String.format("%s :: ", trimmed));
                        splitEvent.setMessage(trimmed);
                        if (trimmed.startsWith("!")) {
                            log.debug("Sending to engine: {}", trimmed);
                            jmsSender.send(HokanModule.HokanIoTelegram, HokanModule.HokanEngine.getQueueName(), "EVENT", splitEvent, false);
                        } else {
                            log.debug("Not a command: {}", trimmed);
                        }
                    }
                } else {
                    if (event.getMessage().startsWith("!")) {
                        log.debug("Sending to engine: {}", message);
                        jmsSender.send(HokanModule.HokanIoTelegram, HokanModule.HokanEngine.getQueueName(), "EVENT", event, false);
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
    public void sendToIrcChannel(IrcMessageEvent event, int chanId) {
        NotifyRequest notifyRequest = new NotifyRequest();
        notifyRequest.setNotifyMessage(String.format("<%s@tg> %s", event.getSender(), event.getMessage()));
        notifyRequest.setTargetChannelId(chanId);
        jmsSender.send(HokanModule.HokanIoTelegram, HokanModule.HokanIo.getQueueName(), "TELEGRAM_NOTIFY_REQUEST", notifyRequest, false);
    }


    @Override
    public void sendServiceRequest(IrcMessageEvent ircEvent, ServiceRequestType requestType) {
        ServiceRequest request = new ServiceRequest(requestType, ircEvent, new CommandArgs(ircEvent.getMessage()), (Object[]) null);
        try {
//            jmsSender.send(HokanModule.HokanServices.getQueueName(), "SERVICE_REQUEST", request, false);
        } catch (Exception e) {
            log.error("error", e);
        }
    }

}
