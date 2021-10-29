package org.freakz.hokan_ng_springboot.bot.engine.service;

import org.freakz.hokan_ng_springboot.bot.common.enums.HokanModule;
import org.freakz.hokan_ng_springboot.bot.common.events.EngineResponse;
import org.freakz.hokan_ng_springboot.bot.common.events.InternalRequest;
import org.freakz.hokan_ng_springboot.bot.common.events.IrcMessageEvent;
import org.freakz.hokan_ng_springboot.bot.common.events.ServiceRequest;
import org.freakz.hokan_ng_springboot.bot.common.jms.JmsEnvelope;
import org.freakz.hokan_ng_springboot.bot.common.jms.api.JmsSender;
import org.freakz.hokan_ng_springboot.bot.common.jms.api.JmsServiceMessageHandler;
import org.freakz.hokan_ng_springboot.bot.engine.command.CommandHandlerService;
import org.freakz.hokan_ng_springboot.bot.engine.command.handlers.Cmd;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;

import java.util.Date;

/**
 * Created by Petri Airio on 10.2.2015.
 * -
 */
@Controller

public class EngineServiceMessageHandlerImpl implements JmsServiceMessageHandler {

    private static final Logger log = LoggerFactory.getLogger(EngineServiceMessageHandlerImpl.class);

    private final ApplicationContext context;

    private final CommandHandlerService commandHandlerService;

    private final JmsSender jmsSender;

    @Autowired
    public EngineServiceMessageHandlerImpl(ApplicationContext context, CommandHandlerService commandHandlerService, JmsSender jmsSender) {
        this.context = context;
        this.commandHandlerService = commandHandlerService;
        this.jmsSender = jmsSender;
    }

    @Override
    public void handleJmsEnvelope(JmsEnvelope envelope) throws Exception {
        IrcMessageEvent event = (IrcMessageEvent) envelope.getMessageIn().getPayLoadObject("EVENT");
        if (event == null) {

            ServiceRequest serviceRequest = (ServiceRequest) envelope.getMessageIn().getPayLoadObject("ENGINE_REQUEST");
            if (serviceRequest == null) {
                log.debug("Nothing to do!");
                return;
            }
            event = serviceRequest.getIrcMessageEvent();
        }

        CmdHandlerMatches matches = commandHandlerService.getMatchingCommands(event.getMessage());
        if (matches.getMatches().size() > 0) {
            if (matches.getMatches().size() == 1) {
                Cmd handler = matches.getMatches().get(0);
                executeHandler(event, handler, envelope);
            } else {
/*                EngineResponse response = new EngineResponse(event);
                String multiple = matches.getFirstWord() + " multiple matches: ";
                for (Cmd match : matches.getMatches()) {
                    multiple += match.getName() + " ";
                }
                response.addResponse(multiple);
                sendReply(response, envelope);
                */
                log.error("TODO!");
            }
        }
    }

    private void sendReply(EngineResponse response, JmsEnvelope envelope) {
        jmsSender.send(HokanModule.HokanEngine, envelope.getMessageIn().getSender().getQueueName(), "ENGINE_RESPONSE", response, false);
    }

    private void executeHandler(IrcMessageEvent event, Cmd handler, JmsEnvelope envelope) {
        EngineResponse response = new EngineResponse(event);

        InternalRequest internalRequest;
        internalRequest = context.getBean(InternalRequest.class);
        internalRequest.setJmsEnvelope(envelope);
        try {
            internalRequest.init(event);
            internalRequest.getUserChannel().setLastCommand(event.getMessage());
            internalRequest.getUserChannel().setLastCommandTime(new Date());
            internalRequest.saveUserChannel();
            handler.handleLine(internalRequest, response);
        } catch (Exception e) {
            log.error("Command handler returned exception {}", e);
        }
    }

}
