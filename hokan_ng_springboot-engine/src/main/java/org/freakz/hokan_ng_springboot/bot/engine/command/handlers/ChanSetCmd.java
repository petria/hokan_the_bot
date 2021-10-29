package org.freakz.hokan_ng_springboot.bot.engine.command.handlers;

import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.UnflaggedOption;
import org.freakz.hokan_ng_springboot.bot.common.events.EngineResponse;
import org.freakz.hokan_ng_springboot.bot.common.events.InternalRequest;
import org.freakz.hokan_ng_springboot.bot.common.exception.HokanException;
import org.freakz.hokan_ng_springboot.bot.common.jpa.entity.Channel;
import org.freakz.hokan_ng_springboot.bot.common.jpa.entity.ChannelStartupState;
import org.freakz.hokan_ng_springboot.bot.engine.command.HelpGroup;
import org.freakz.hokan_ng_springboot.bot.engine.command.annotation.HelpGroups;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import static org.freakz.hokan_ng_springboot.bot.common.util.StaticStrings.ARG_CHANNEL_ID;
import static org.freakz.hokan_ng_springboot.bot.common.util.StaticStrings.ARG_STARTUP_STATE;

/**
 * Created by Petri Airio on 16.10.2015.
 * -
 */
@Component
@Scope("prototype")

@HelpGroups(
        helpGroups = {HelpGroup.CHANNELS, HelpGroup.PROPERTIES}
)
public class ChanSetCmd extends Cmd {

    public ChanSetCmd() {
        super();
        setHelp("Sets Channel modes");

        UnflaggedOption unflaggedOption = new UnflaggedOption(ARG_STARTUP_STATE)
                .setRequired(true)
                .setGreedy(false);
        registerParameter(unflaggedOption);

        unflaggedOption = new UnflaggedOption(ARG_CHANNEL_ID)
                .setRequired(false)
                .setGreedy(false);
        registerParameter(unflaggedOption);

        setAdminUserOnly(true);

    }

    @Override
    public void handleRequest(InternalRequest request, EngineResponse response, JSAPResult results) throws HokanException {
        String channelId = results.getString(ARG_CHANNEL_ID, null);
        if (request.getIrcEvent().isPrivate() && channelId == null) {
            response.addResponse("ChannelID parameter is needed when using private message, try: !chanlist to get ID.");
            return;
        }
        Channel theChannel = request.getChannel();
        if (channelId != null) {
            long id;
            try {
                id = Long.parseLong(channelId);
            } catch (NumberFormatException ex) {
                response.addResponse("Valid ChannelID parameter is needed, try: !chanlist");
                return;
            }
            theChannel = channelService.findOne(id);
            if (theChannel == null) {
                response.addResponse("No valid Channel found with id: %d, try: !chanlist to get ID.", id);
                return;
            }
        }

        String startupState = results.getString(ARG_STARTUP_STATE);
        ChannelStartupState state = ChannelStartupState.valueOf(startupState);
        if (state != null) {
            theChannel.setChannelStartupState(state);
            theChannel = channelService.save(theChannel);
            response.addResponse("%s startup state set to: %s", theChannel.getChannelName(), theChannel.getChannelStartupState());
        } else {
            response.addResponse("Invalid startup state: %s", startupState);
        }
    }

}
