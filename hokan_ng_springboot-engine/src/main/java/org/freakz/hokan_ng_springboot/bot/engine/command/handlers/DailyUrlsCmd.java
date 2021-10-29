package org.freakz.hokan_ng_springboot.bot.engine.command.handlers;

import com.martiansoftware.jsap.JSAPResult;
import org.freakz.hokan_ng_springboot.bot.common.events.EngineResponse;
import org.freakz.hokan_ng_springboot.bot.common.events.InternalRequest;
import org.freakz.hokan_ng_springboot.bot.common.exception.HokanException;
import org.freakz.hokan_ng_springboot.bot.common.jpa.entity.Url;
import org.freakz.hokan_ng_springboot.bot.common.util.StringStuff;
import org.freakz.hokan_ng_springboot.bot.common.util.TimeUtil;
import org.freakz.hokan_ng_springboot.bot.engine.command.HelpGroup;
import org.freakz.hokan_ng_springboot.bot.engine.command.annotation.HelpGroups;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * Created by Petri Airio on 22.9.2015.
 */
@Component
@Scope("prototype")

@HelpGroups(
        helpGroups = {HelpGroup.DATA_COLLECTION}
)
public class DailyUrlsCmd extends Cmd {

    public DailyUrlsCmd() {
        super();
        setHelp("Find urls pasted to channel in certain day. If no day is supplied today is used.");

        // TODO add DAY parameter

    }

    @Override
    public void handleRequest(InternalRequest request, EngineResponse response, JSAPResult results) throws HokanException {
        LocalDateTime time = LocalDateTime.now();
        List<Url> urlList = urlLoggerService.findByCreatedBetweenAndChannel(TimeUtil.getStartAndEndTimeForDay(time), request.getChannel().getChannelName());
        if (urlList.size() > 0) {
            int shown = 0;
            String ret = null;

            for (Url row : urlList) {

                if (ret == null) {
                    ret = String.format("Daily URLs for date: %s\n", StringStuff.formatTime(new Date(), StringStuff.STRING_STUFF_DF_DDMMYYYY));
                }

                if (shown > 0) {
                    ret += " ";
                }

                shown++;

/*        if (shown == 5) {
          break;
        }*/

                ret += shown + ") " + row.getSender() + ": ";
                ret += row.getUrl();
/*        if (row.getUrlTitle() != null) {
          ret += " \"t: " + row.getUrlTitle() + "\"";
        }
        ret += " [" + StringStuff.formatNiceDate(row.getCreated(), false) + "]";*/
                response.addResponse(ret);
            }

        } else {
            response.addResponse("No urls found for date: %s", time.toString());
        }
    }

}
