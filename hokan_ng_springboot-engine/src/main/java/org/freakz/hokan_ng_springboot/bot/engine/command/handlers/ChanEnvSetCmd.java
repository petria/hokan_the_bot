package org.freakz.hokan_ng_springboot.bot.engine.command.handlers;

import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.UnflaggedOption;
import org.freakz.hokan_ng_springboot.bot.common.events.EngineResponse;
import org.freakz.hokan_ng_springboot.bot.common.events.InternalRequest;
import org.freakz.hokan_ng_springboot.bot.common.exception.HokanException;
import org.freakz.hokan_ng_springboot.bot.common.jpa.entity.Channel;
import org.freakz.hokan_ng_springboot.bot.common.jpa.entity.ChannelPropertyEntity;
import org.freakz.hokan_ng_springboot.bot.common.jpa.entity.PropertyName;
import org.freakz.hokan_ng_springboot.bot.common.util.StringStuff;
import org.freakz.hokan_ng_springboot.bot.engine.command.HelpGroup;
import org.freakz.hokan_ng_springboot.bot.engine.command.annotation.HelpGroups;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import static org.freakz.hokan_ng_springboot.bot.common.util.StaticStrings.ARG_CHANNEL_ID;
import static org.freakz.hokan_ng_springboot.bot.common.util.StaticStrings.ARG_PROPERTY;

/**
 * User: petria
 * Date: 12/10/13
 * Time: 3:02 PM
 *
 * @author Petri Airio <petri.j.airio@gmail.com>
 */
@Component
@Scope("prototype")

@HelpGroups(
        helpGroups = {HelpGroup.CHANNELS, HelpGroup.PROPERTIES}
)
public class ChanEnvSetCmd extends Cmd {

    public PropertyName getPropertyName(String property) {
        for (PropertyName prop : PropertyName.values()) {
            if (StringStuff.match(prop.toString(), property, true)) {
                return prop;
            }
        }
        return null;
    }

    public ChanEnvSetCmd() {

        super();
        setHelp("Sets channel property. When executed via private messages valid channel id must be given.");
        setChannelOpOnly(true);

        UnflaggedOption unflaggedOption = new UnflaggedOption(ARG_PROPERTY)
                .setRequired(true)
                .setGreedy(false);
        registerParameter(unflaggedOption);

        unflaggedOption = new UnflaggedOption(ARG_CHANNEL_ID)
                .setRequired(false)
                .setGreedy(false);
        registerParameter(unflaggedOption);

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

        String[] split = results.getString(ARG_PROPERTY).split("=");
        if (split.length != 2) {
            response.addResponse("Syntax error, usage: %s <PropertyName>=<Value>", getName());
            return;
        }

        PropertyName propertyName = getPropertyName(split[0]);
        if (propertyName == null) {
            response.addResponse("Invalid property: %s", split[0]);
            return;
        }
        ChannelPropertyEntity chanProp = channelPropertyService.setChannelProperty(theChannel, propertyName, split[1]);
        response.addResponse("Channel property set to: %s", chanProp.toString());
    }


}
