package org.freakz.hokan_ng_springboot.bot.engine.command.handlers;

import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.UnflaggedOption;
import org.freakz.hokan_ng_springboot.bot.common.events.EngineResponse;
import org.freakz.hokan_ng_springboot.bot.common.events.InternalRequest;
import org.freakz.hokan_ng_springboot.bot.common.exception.HokanException;
import org.freakz.hokan_ng_springboot.bot.common.jpa.entity.User;
import org.freakz.hokan_ng_springboot.bot.common.jpa.entity.UserFlag;
import org.freakz.hokan_ng_springboot.bot.engine.command.HelpGroup;
import org.freakz.hokan_ng_springboot.bot.engine.command.annotation.HelpGroups;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Set;

import static org.freakz.hokan_ng_springboot.bot.common.util.StaticStrings.ARG_FLAGS;
import static org.freakz.hokan_ng_springboot.bot.common.util.StaticStrings.ARG_NICK;

/**
 * Created by Petri Airio on 24.2.2016.
 * -
 */
@Component

@Scope("prototype")
@HelpGroups(
        helpGroups = {HelpGroup.ACCESS_CONTROL, HelpGroup.USERS}
)
public class UserFlagAddCmd extends Cmd {

    public UserFlagAddCmd() {

        setHelp("Adds user flags.");
        setHelpWikiUrl("https://github.com/petria/hokan_ng_springboot/wiki/UserFlags");
        setAdminUserOnly(true);

        UnflaggedOption unflaggedOption = new UnflaggedOption(ARG_NICK)
                .setRequired(true)
                .setGreedy(false);
        registerParameter(unflaggedOption);

        unflaggedOption = new UnflaggedOption(ARG_FLAGS)
                .setRequired(false)
                .setGreedy(false);
        registerParameter(unflaggedOption);

    }

    @Override
    public void handleRequest(InternalRequest request, EngineResponse response, JSAPResult results) throws HokanException {
        String target = results.getString(ARG_NICK, null);

        User user;
        if (target.equals("me")) {
            user = request.getUser();
        } else {
            user = userService.findFirstByNick(target);
        }
        if (user == null) {
            response.addResponse("No User found with: " + target);
            return;
        }

        String flagsStr = results.getString(ARG_FLAGS, null);
        if (flagsStr == null) {
            response.addResponse("%s UserFlags: %s", user.getNick(), UserFlag.getStringFromFlagSet(user));
            return;
        }
        Set<UserFlag> flags = UserFlag.getFlagSetFromString(flagsStr);
        if (flags.size() == 0) {
            response.addResponse("No flags: " + flagsStr);
            return;
        }
        user = accessControlService.addUserFlags(user, flags);
        response.addResponse("%s flags now: %s", user.getNick(), UserFlag.getStringFromFlagSet(user.getUserFlagsSet()));
    }

}
