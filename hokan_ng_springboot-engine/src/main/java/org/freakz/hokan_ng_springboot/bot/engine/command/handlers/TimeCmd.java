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

import java.time.LocalDateTime;
import java.util.TimeZone;

import static org.freakz.hokan_ng_springboot.bot.common.util.StaticStrings.ARG_TIMEZONE;

@Component
@Scope("prototype")
@HelpGroups(
        helpGroups = {HelpGroup.SYSTEM}
)
public class TimeCmd extends Cmd {

    public TimeCmd() {

        setHelp("Shows current system time with optional TIMEZONE");

        UnflaggedOption unflaggedOption = new UnflaggedOption(ARG_TIMEZONE)
                .setRequired(false)
                .setGreedy(false);
        registerParameter(unflaggedOption);

    }

    @Override
    public void handleRequest(InternalRequest request, EngineResponse response, JSAPResult results) throws HokanException {
        String zoneName = results.getString(ARG_TIMEZONE, null);
        if (zoneName == null) {
            zoneName = getLocalZone();
        }

        String time;
        TimeZone timeZone = TimeZone.getTimeZone(zoneName);
        if (timeZone != null) {
            LocalDateTime now = LocalDateTime.now(timeZone.toZoneId());

            time = now.toString();
        } else {
            LocalDateTime now = LocalDateTime.now();
            time = now.toString();
        }

        response.addResponse(timeZone.getDisplayName() + " ->: " + time);

    }

    private String getLocalZone() {
        return "EET";
    }
}
