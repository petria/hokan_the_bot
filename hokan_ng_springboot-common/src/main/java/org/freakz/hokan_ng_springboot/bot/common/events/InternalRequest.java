package org.freakz.hokan_ng_springboot.bot.common.events;

import org.freakz.hokan_ng_springboot.bot.common.exception.HokanException;
import org.freakz.hokan_ng_springboot.bot.common.jms.JmsEnvelope;
import org.freakz.hokan_ng_springboot.bot.common.jpa.entity.*;
import org.freakz.hokan_ng_springboot.bot.common.jpa.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * User: petria
 * Date: 12/10/13
 * Time: 3:45 PM
 *
 * @author Petri Airio <petri.j.airio@gmail.com>
 */
@Component
@Scope("prototype")
public class InternalRequest implements Serializable {

    private static final Logger log = LoggerFactory.getLogger(InternalRequest.class);

    @Autowired
    private NetworkService networkService;

    @Autowired
    private ChannelService channelService;

    @Autowired
    private ChannelStatsService channelStatsService;

    @Autowired
    private UserChannelService userChannelService;

    @Autowired
    private UserService userService;


    private Network network;
    private Channel channel;
    private User user;
    private UserChannel userChannel;
    private ChannelStats channelStats;

    private IrcMessageEvent ircMessageEvent;
    private JmsEnvelope envelope;

    public InternalRequest() {
    }

    public void init(IrcMessageEvent ircMessageEvent) throws HokanException {
        this.ircMessageEvent = ircMessageEvent;
        this.network = networkService.getNetwork(ircMessageEvent.getNetwork());
        this.user = getUser(ircMessageEvent);
        this.channel = channelService.findByNetworkAndChannelName(network, ircMessageEvent.getChannel());
        this.userChannel = userChannelService.getUserChannel(this.user, this.channel);
        this.channelStats = channelStatsService.findFirstByChannel(channel);

    }


    public IrcMessageEvent getIrcEvent() {
        return this.ircMessageEvent;
    }

    public Network getNetwork() {
        return network;
    }

    public Channel getChannel() {
        return channel;
    }

    public User getUser() {
        return user;
    }

    public UserChannel getUserChannel() {
        return userChannel;
    }

    public void saveUserChannel() {
        this.userChannel = userChannelService.save(this.userChannel);
    }

    public void saveChannelStats() {
        this.channelStats = channelStatsService.save(this.channelStats);
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
        return user;
    }

    public ChannelStats getChannelStats() {
        return channelStats;
    }

    public JmsEnvelope getJmsEnvelope() {
        return envelope;
    }

    public void setJmsEnvelope(JmsEnvelope envelope) {
        this.envelope = envelope;
    }
}
