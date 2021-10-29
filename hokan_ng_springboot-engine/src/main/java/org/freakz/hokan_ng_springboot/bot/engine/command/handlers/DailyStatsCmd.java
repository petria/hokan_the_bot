package org.freakz.hokan_ng_springboot.bot.engine.command.handlers;

import com.martiansoftware.jsap.JSAPResult;
import org.freakz.hokan_ng_springboot.bot.common.events.EngineResponse;
import org.freakz.hokan_ng_springboot.bot.common.events.InternalRequest;
import org.freakz.hokan_ng_springboot.bot.common.exception.HokanException;
import org.freakz.hokan_ng_springboot.bot.common.models.StatsData;
import org.freakz.hokan_ng_springboot.bot.common.models.StatsMapper;
import org.freakz.hokan_ng_springboot.bot.common.util.StringStuff;
import org.freakz.hokan_ng_springboot.bot.common.util.TimeUtil;
import org.freakz.hokan_ng_springboot.bot.engine.command.HelpGroup;
import org.freakz.hokan_ng_springboot.bot.engine.command.annotation.HelpGroups;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by Petri Airio on 24.8.2015.
 */
@Component
@Scope("prototype")
@HelpGroups(
        helpGroups = {HelpGroup.DATA_COLLECTION}
)
public class DailyStatsCmd extends Cmd {

    public DailyStatsCmd() {
        super();
        setHelp("Show daily stats, words written to specific channel.");
    }

    @Override
    public void handleRequest(InternalRequest request, EngineResponse response, JSAPResult results) throws HokanException {
        LocalDateTime today = LocalDateTime.now();
        StatsMapper statsMapper = statsService.getDailyStatsForChannel(today, request.getChannel().getChannelName());
        if (!statsMapper.hasError()) {
            List<StatsData> statsDatas = statsMapper.getStatsData();
            String res = StringStuff.formatTime(TimeUtil.localDateTimeToDate(today), StringStuff.STRING_STUFF_DF_DDMMYYYY) + " top words:";
            int i = 1;
            for (StatsData statsData : statsDatas) {
                res += " " + i + ") " + statsData.getNick() + "=" + statsData.getWords();
                i++;
            }
            response.addResponse(res);
        } else {
            response.addResponse(statsMapper.getError());
        }
    }

}
