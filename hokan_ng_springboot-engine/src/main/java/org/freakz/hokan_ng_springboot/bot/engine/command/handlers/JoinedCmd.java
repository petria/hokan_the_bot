package org.freakz.hokan_ng_springboot.bot.engine.command.handlers;

import com.martiansoftware.jsap.JSAPResult;
import org.freakz.hokan_ng_springboot.bot.common.events.EngineResponse;
import org.freakz.hokan_ng_springboot.bot.common.events.InternalRequest;
import org.freakz.hokan_ng_springboot.bot.common.exception.HokanException;
import org.freakz.hokan_ng_springboot.bot.common.jpa.entity.Channel;
import org.freakz.hokan_ng_springboot.bot.common.jpa.entity.ChannelState;
import org.freakz.hokan_ng_springboot.bot.common.jpa.entity.JoinedUser;
import org.freakz.hokan_ng_springboot.bot.common.jpa.entity.Network;
import org.freakz.hokan_ng_springboot.bot.common.jpa.service.ChannelService;
import org.freakz.hokan_ng_springboot.bot.common.jpa.service.JoinedUserService;
import org.freakz.hokan_ng_springboot.bot.common.jpa.service.NetworkService;
import org.freakz.hokan_ng_springboot.bot.engine.command.HelpGroup;
import org.freakz.hokan_ng_springboot.bot.engine.command.annotation.HelpGroups;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * User: petria
 * Date: 03-Mar-2009
 * Time: 09:08:27
 *
 * @author Petri Airio (petri.j.airio@gmail.com) *
 */
@Component
@Scope("prototype")
@HelpGroups(
        helpGroups = {HelpGroup.CHANNELS}
)
public class JoinedCmd extends Cmd {

    @Autowired
    private ChannelService channelService;

    @Autowired
    private JoinedUserService joinedUsersService;

    @Autowired
    private NetworkService networkService;

    public JoinedCmd() {
        super();
        setHelp("Shows channel where the Bot currently is joined.");
    }


/*  public String getMatchPattern() {
    return "!joined";
  }*/

    @Override
    public void handleRequest(InternalRequest request, EngineResponse response, JSAPResult results) throws HokanException {

        StringBuilder sb = new StringBuilder();
        List<Network> networks = networkService.findAll();
        for (Network network : networks) {
            List<Channel> channels = channelService.findChannels(network, ChannelState.JOINED);
            if (channels.size() > 0) {
                sb.append(String.format("[%s]\n", network.getName()));
                for (Channel channel : channels) {
                    if (channels.size() > 0) {
                        sb.append(String.format("  %s (%d)\n", channel.getChannelName(), channel.getId()));
                        List<JoinedUser> joinedUsers = joinedUsersService.findJoinedUsers(channel);
                        sb.append("    ");
                        for (JoinedUser joinedUser : joinedUsers) {
                            if (joinedUser.isOp()) {
                                sb.append("@");
                            }
                            if (joinedUser.hasVoice()) {
                                sb.append("+");
                            }
                            sb.append(joinedUser.getUser().getNick());
                            sb.append(" ");
                        }
                        sb.append("\n");
                    }
                }
            }
        }
        response.setResponseMessage(sb.toString());
    }
}
