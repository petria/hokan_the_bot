package org.freakz.hokan_ng_springboot.bot.engine.command.handlers;

import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.UnflaggedOption;
import org.freakz.hokan_ng_springboot.bot.common.enums.TopCountsEnum;
import org.freakz.hokan_ng_springboot.bot.common.events.EngineResponse;
import org.freakz.hokan_ng_springboot.bot.common.events.InternalRequest;
import org.freakz.hokan_ng_springboot.bot.common.exception.HokanException;
import org.freakz.hokan_ng_springboot.bot.common.models.DataValueStatsModel;
import org.freakz.hokan_ng_springboot.bot.engine.command.HelpGroup;
import org.freakz.hokan_ng_springboot.bot.engine.command.annotation.HelpGroups;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import static org.freakz.hokan_ng_springboot.bot.common.util.StaticStrings.ARG_NICK;
import static org.freakz.hokan_ng_springboot.bot.common.util.StaticStrings.ARG_TOP_KEY;

/**
 * Created by Petri Airio on 26.8.2015.
 * -
 */
@Component
@HelpGroups(
        helpGroups = {HelpGroup.TOP_STATS}
)
@Scope("prototype")
public class TopStatsCmd extends Cmd {

    public TopStatsCmd() {
        super();
        setHelp("Stats about counted thing, TopKey parameter need to one of: " + String.join(", ", TopCountsEnum.getPrettyNames()));
        setChannelOnly(true);

        UnflaggedOption uflg = new UnflaggedOption(ARG_TOP_KEY)
                .setRequired(false)
                .setGreedy(false);
        registerParameter(uflg);

        UnflaggedOption opt = new UnflaggedOption(ARG_NICK)
                .setDefault("me")
                .setRequired(false)
                .setGreedy(false);
        registerParameter(opt);

    }

    private String mustBeError() {
        return String.format("%s\n  TopKey must be one of: %s", getUsage(), String.join(", ", TopCountsEnum.getPrettyNames()));
    }

    @Override
    public void handleRequest(InternalRequest request, EngineResponse response, JSAPResult results) throws HokanException {
        String topKey = results.getString(ARG_TOP_KEY);
        if (topKey == null) {
            response.setResponseMessage(mustBeError());
            return;
        }
        TopCountsEnum countsEnum = TopCountsEnum.getByPrettyName(topKey);
        if (countsEnum == null) {
            response.setResponseMessage(mustBeError());
            return;
        }


        String channel = request.getIrcEvent().getChannel().toLowerCase();
        String network = request.getIrcEvent().getNetwork().toLowerCase();
        String nick;
        if (results.getString(ARG_NICK).equals("me")) {
            nick = request.getIrcEvent().getSender().toLowerCase();
        } else {
            nick = results.getString(ARG_NICK).toLowerCase();
        }

        StringBuilder sb = new StringBuilder();

        DataValueStatsModel stats = dataValuesService.getValueStats(nick, channel, network, countsEnum.getKeyName());
        if (stats == null) {
            sb.append(nick);
            sb.append("'s need to start ").append(countsEnum.getName()).append(" ASAP!!");
        } else {
            sb.append(nick);
            sb.append("'s ").append(countsEnum.getName()).append(" stats so far: ");
            sb.append(stats.getOutput());
        }
        response.addResponse(sb.toString());
    }
}
