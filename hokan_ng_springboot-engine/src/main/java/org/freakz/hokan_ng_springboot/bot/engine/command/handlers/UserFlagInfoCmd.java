package org.freakz.hokan_ng_springboot.bot.engine.command.handlers;

import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.UnflaggedOption;
import org.freakz.hokan_ng_springboot.bot.common.events.EngineResponse;
import org.freakz.hokan_ng_springboot.bot.common.events.InternalRequest;
import org.freakz.hokan_ng_springboot.bot.common.exception.HokanException;
import org.freakz.hokan_ng_springboot.bot.engine.command.HelpGroup;
import org.freakz.hokan_ng_springboot.bot.engine.command.annotation.HelpGroups;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import static org.freakz.hokan_ng_springboot.bot.common.util.StaticStrings.ARG_FLAGS;

/**
 * Created by Petri Airio on 30.5.2016.
 * -
 */
@Component

@Scope("prototype")
@HelpGroups(
        helpGroups = {HelpGroup.ACCESS_CONTROL, HelpGroup.USERS}
)
public class UserFlagInfoCmd extends Cmd {

    public UserFlagInfoCmd() {

        setHelp("");
        setHelpWikiUrl("https://github.com/petria/hokan_ng_springboot/wiki/UserFlags");
//    setAdminUserOnly(true);

        UnflaggedOption unflaggedOption = new UnflaggedOption(ARG_FLAGS)
                .setRequired(false)
                .setGreedy(false);
        registerParameter(unflaggedOption);

    }

    @Override
    public void handleRequest(InternalRequest request, EngineResponse response, JSAPResult results) throws HokanException {
        String flagStr = results.getString(ARG_FLAGS, null);
        if (flagStr == null) {
            response.addResponse("Available User Flags: ");
        } else {
            response.addResponse("User Flag: %s", flagStr);
        }
    }

}
