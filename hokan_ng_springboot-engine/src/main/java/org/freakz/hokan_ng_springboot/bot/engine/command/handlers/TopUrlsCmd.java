package org.freakz.hokan_ng_springboot.bot.engine.command.handlers;

import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.UnflaggedOption;
import org.freakz.hokan_ng_springboot.bot.common.events.EngineResponse;
import org.freakz.hokan_ng_springboot.bot.common.events.InternalRequest;
import org.freakz.hokan_ng_springboot.bot.common.exception.HokanException;
import org.freakz.hokan_ng_springboot.bot.common.jpa.entity.Url;
import org.freakz.hokan_ng_springboot.bot.engine.command.HelpGroup;
import org.freakz.hokan_ng_springboot.bot.engine.command.annotation.HelpGroups;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.freakz.hokan_ng_springboot.bot.common.util.StaticStrings.ARG_CHANNEL;

/**
 * Created by pairio on 18.6.2014.
 *
 * @author Petri Airio (petri.j.airio@gmail.com)
 */
@Component
@Scope("prototype")

@HelpGroups(
        helpGroups = {HelpGroup.URLS}
)
public class TopUrlsCmd extends Cmd {

    public TopUrlsCmd() {
        super();
        setHelp("Shows top url posters in channel. ");

        UnflaggedOption uflg = new UnflaggedOption(ARG_CHANNEL)
                .setRequired(false)
                .setGreedy(false);
        registerParameter(uflg);

    }

    @Override
    public void handleRequest(InternalRequest request, EngineResponse response, JSAPResult results) throws HokanException {
        String channel = results.getString(ARG_CHANNEL);
        if (channel == null) {
            channel = request.getChannel().getChannelName();
        }
        List counts;
        String ret;
        if (channel.equals("*")) {
            counts = urlLoggerService.findTopSender();
            ret = "Top urls sender for all channels: ";
        } else {
            counts = urlLoggerService.findTopSenderByChannel(channel);
            ret = "Top urls sender for channel " + channel + ": ";
        }
        int max_count = 9;
        for (int i = 0; i < counts.size(); i++) {
            Object[] counter = (Object[]) counts.get(i);
            Url url = (Url) counter[0];
            Long count = (Long) counter[1];
            if (i > 0) {
                ret += ", ";
            }
            ret += (i + 1) + ") " + url.getSender() + "=" + count;
            if (i == max_count) {
                break;
            }
        }
        long countTotal = 0;
        for (Object count1 : counts) {
            Object[] counter = (Object[]) count1;
            Url url = (Url) counter[0];
            Long count = (Long) counter[1];
            countTotal += count;
        }
        ret += " - Urls count = " + countTotal;

        response.addResponse(ret);

    }
}
