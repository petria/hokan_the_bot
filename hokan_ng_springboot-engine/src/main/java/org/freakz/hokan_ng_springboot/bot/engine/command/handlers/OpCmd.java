package org.freakz.hokan_ng_springboot.bot.engine.command.handlers;

import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.UnflaggedOption;
import org.freakz.hokan_ng_springboot.bot.common.events.EngineResponse;
import org.freakz.hokan_ng_springboot.bot.common.events.InternalRequest;
import org.freakz.hokan_ng_springboot.bot.common.exception.HokanException;
import org.freakz.hokan_ng_springboot.bot.common.jpa.entity.JoinedUser;
import org.freakz.hokan_ng_springboot.bot.engine.command.HelpGroup;
import org.freakz.hokan_ng_springboot.bot.engine.command.annotation.HelpGroups;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.freakz.hokan_ng_springboot.bot.common.util.StaticStrings.ARG_TARGET;

/**
 * User: petria
 * Date: 12/19/13
 * Time: 7:25 PM
 *
 * @author Petri Airio <petri.j.airio@gmail.com>
 */
@Component
@Scope("prototype")

@HelpGroups(
        helpGroups = {HelpGroup.ACCESS_CONTROL}
)
public class OpCmd extends Cmd {

    public OpCmd() {
        super();
        setHelp("Gives operator rights to target user on channel.");

        UnflaggedOption flg = new UnflaggedOption(ARG_TARGET)
                .setRequired(false)
                .setGreedy(false);
        registerParameter(flg);

        setLoggedInOnly(false);
        setChannelOpOnly(false);

    }

    @Override
    public void handleRequest(InternalRequest request, EngineResponse response, JSAPResult results) throws HokanException {

        String target = results.getString(ARG_TARGET);
        if (request.getIrcEvent().isPrivate()) {
            if (target == null) {
                target = request.getIrcEvent().getSender();
            }
            List<JoinedUser> joinedUsers = joinedUsersService.findJoinedUsersByNetwork(request.getNetwork());
            boolean didOp = false;
            for (JoinedUser joinedUser : joinedUsers) {
                if (joinedUser.getUser().getNick().toLowerCase().matches(".*" + target.toLowerCase() + ".*")) {
                    response.addEngineMethodCall("op", joinedUser.getChannel().getChannelName(), joinedUser.getUser().getNick());
                    response.addResponse("Trying to op %s on channel %s\n", joinedUser.getUser().getNick(), joinedUser.getChannel().getChannelName());
                    didOp = true;
                }
            }
            if (!didOp) {
                response.addResponse("Didn't find anyone matching %s !", target);
            }

        } else {

            if (!request.getIrcEvent().isBotOp()) {
                response.addResponse("I need OPs!");
                return;
            }

            if (target == null) {
                response.addEngineMethodCall("op", request.getChannel().getChannelName(), request.getIrcEvent().getSender());
            } else {
                response.addEngineMethodCall("op", request.getChannel().getChannelName(), target);
            }
        }
    }

}
