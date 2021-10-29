package org.freakz.hokan_ng_springboot.bot.io.service;

import org.freakz.hokan_ng_springboot.bot.common.jms.JmsEnvelope;
import org.freakz.hokan_ng_springboot.bot.common.jms.api.JmsServiceMessageHandler;
import org.freakz.hokan_ng_springboot.bot.common.jpa.service.ChannelService;
import org.freakz.hokan_ng_springboot.bot.common.jpa.service.JoinedUserService;
import org.freakz.hokan_ng_springboot.bot.common.service.ConnectionManagerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 * Created by Petri Airio on 10.2.2015.
 * -
 */
@Controller
public class IoServiceMessageHandlerImpl implements JmsServiceMessageHandler {

    private static final Logger log = LoggerFactory.getLogger(IoServiceMessageHandlerImpl.class);

    @Autowired
    private ConnectionManagerService connectionManagerService;

    @Autowired
    private ChannelService channelService;

    @Autowired
    private JoinedUserService joinedUserService;


    @Override
    public void handleJmsEnvelope(JmsEnvelope envelope) throws Exception {

    }

/*  @Override
  public JmsMessage handleJmsServiceMessage(JmsMessage jmsMessage) {
//    log.debug("Handling message");
    String command = (String) jmsMessage.getPayLoadObject("COMMAND");
    JmsMessage reply = new JmsMessage();
    switch (command) {
      case "GO_ONLINE":
        try {
          connectionManagerService.connect("DEVNET");
          reply.addPayLoadObject("REPLY", "Connecting to: DEVNET");
        } catch (Exception e) {
          reply.addPayLoadObject("REPLY", e.getMessage());
        }
        break;
      case "TEST":
        List<Channel> channels = channelService.findAll();
        Channel channel = channels.get(0);
        joinedUserService.clearJoinedUsers(channel);
        break;
    }
    return reply;
  }
  */
}
