package org.freakz.hokan_ng_springboot.bot.engine.command.handlers;

import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.UnflaggedOption;
import org.freakz.hokan_ng_springboot.bot.common.events.EngineResponse;
import org.freakz.hokan_ng_springboot.bot.common.events.InternalRequest;
import org.freakz.hokan_ng_springboot.bot.common.exception.HokanException;
import org.freakz.hokan_ng_springboot.bot.common.jpa.entity.Channel;
import org.freakz.hokan_ng_springboot.bot.common.jpa.service.ChannelPropertyService;
import org.freakz.hokan_ng_springboot.bot.engine.command.HelpGroup;
import org.freakz.hokan_ng_springboot.bot.engine.command.annotation.HelpGroups;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.freakz.hokan_ng_springboot.bot.common.util.StaticStrings.ARG_CHANNEL_ID;

/**
 * User: petria
 * Date: 12/11/13
 * Time: 2:20 PM
 *
 * @author Petri Airio <petri.j.airio@gmail.com>
 */
@Component
@Scope("prototype")
@HelpGroups(
        helpGroups = {HelpGroup.PROPERTIES}
)
public class ChanEnvCmd extends Cmd {

    @Autowired
    private ChannelPropertyService properties;

    public ChanEnvCmd() {
        super();
        setHelp("Shows properties set for the channel");

        UnflaggedOption flg = new UnflaggedOption(ARG_CHANNEL_ID)
                .setRequired(false)
                .setGreedy(false);
        registerParameter(flg);

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

        List propertyList = properties.findByChannel(theChannel);
        for (Object property : propertyList) {
            response.addResponse("%s\n", property.toString());
        }
    }
}
