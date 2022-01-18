package org.freakz.hokan_ng_springboot.bot.engine.command.handlers;

import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.UnflaggedOption;
import org.freakz.hokan_ng_springboot.bot.common.enums.TopCountsEnum;
import org.freakz.hokan_ng_springboot.bot.common.events.EngineResponse;
import org.freakz.hokan_ng_springboot.bot.common.events.InternalRequest;
import org.freakz.hokan_ng_springboot.bot.common.exception.HokanException;
import org.freakz.hokan_ng_springboot.bot.common.models.DataValuesModel;
import org.freakz.hokan_ng_springboot.bot.engine.command.HelpGroup;
import org.freakz.hokan_ng_springboot.bot.engine.command.annotation.HelpGroups;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.freakz.hokan_ng_springboot.bot.common.util.StaticStrings.ARG_CHANNEL;
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
public class CountsPosCmd extends Cmd {

    public CountsPosCmd() {
        super();
        setHelp("Your position in top stats. TopKey parameter need to one of: " + String.join(", ", TopCountsEnum.getPrettyNames()));

        UnflaggedOption uflg = new UnflaggedOption(ARG_TOP_KEY)
                .setRequired(false)
                .setGreedy(false);
        registerParameter(uflg);

        uflg = new UnflaggedOption(ARG_CHANNEL)
                .setRequired(false)
                .setGreedy(false);
        registerParameter(uflg);

    }

    class PositionChange {
        DataValuesModel ahead = null;
        DataValuesModel own = null;
        int position = 0;
        DataValuesModel after = null;
    }

    private PositionChange getNickPosition(String channel, String network, String key, String nick) {
        List<DataValuesModel> dataValues = dataValuesService.getDataValuesAsc(channel, network, key);
        if (dataValues.size() > 0) {
            for (int i = 0; i < dataValues.size(); i++) {
                if (dataValues.get(i).getNick().equalsIgnoreCase(nick)) {
                    PositionChange change = new PositionChange();
                    change.own = dataValues.get(i);
                    change.position = i + 1;
                    if (i > 0) {
                        change.ahead = dataValues.get(i - 1);
                    }
                    if (i != dataValues.size() - 1) {
                        change.after = dataValues.get(i + 1);
                    }
                    return change;
                }
            }
        }
        return null;
    }

    @Override
    public void handleRequest(InternalRequest request, EngineResponse response, JSAPResult results) throws HokanException {
        String key = results.getString(ARG_TOP_KEY);
        if (key == null) {
            String ret = String.format("%s\n  TopKey must be one of: %s", getUsage(), formatKeys());
            response.setResponseMessage(ret);
            return;
        }

        String channel;
        if (request.getIrcEvent().isPrivate()) {
            channel = "#amigafin";
        } else {
            channel = results.getString(ARG_CHANNEL, request.getIrcEvent().getChannel()).toLowerCase();
        }

        String nick = request.getIrcEvent().getSender().toLowerCase();


        String network = request.getIrcEvent().getNetwork().toLowerCase();
        PositionChange newPos = getNickPosition(channel, network, key, nick);
        if (newPos != null) {

            String posText;
            if (newPos.position == 1) {
                posText = String.format("*%s*  \u0002%d. you = %s\u0002 <--> %d. %s = %s",
                        key,
                        newPos.position,
                        newPos.own.getValue(),
                        newPos.position + 1,
                        newPos.after.getNick(),
                        newPos.after.getValue()
                );

            } else {
                posText = String.format("*%s*  %d. %s = %s <--> \u0002%d. you = %s\u0002 <--> %d. %s = %s",
                        key,
                        newPos.position - 1,
                        newPos.ahead.getNick(),
                        newPos.ahead.getValue(),
                        newPos.position,
                        newPos.own.getValue(),
                        newPos.position + 1,
                        newPos.after.getNick(),
                        newPos.after.getValue()
                );

            }
            response.addResponse(posText);
        }

    }

    private String formatKeys() {
        return String.join(", ", TopCountsEnum.getPrettyNames());
    }

}
