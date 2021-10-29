package org.freakz.hokan_ng_springboot.bot.engine.command.handlers;

import com.martiansoftware.jsap.JSAPResult;
import org.freakz.hokan_ng_springboot.bot.common.events.EngineResponse;
import org.freakz.hokan_ng_springboot.bot.common.events.InternalRequest;
import org.freakz.hokan_ng_springboot.bot.common.events.ServiceRequestType;
import org.freakz.hokan_ng_springboot.bot.common.events.ServiceResponse;
import org.freakz.hokan_ng_springboot.bot.common.exception.HokanException;
import org.freakz.hokan_ng_springboot.bot.common.models.NimipaivaData;
import org.freakz.hokan_ng_springboot.bot.common.util.StringStuff;
import org.freakz.hokan_ng_springboot.bot.common.util.TimeUtil;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.Locale;

/**
 * Created by Petri Airio on 8.10.2015.
 */
@Component
@Scope("prototype")

public class TodayCmd extends Cmd {

    public TodayCmd() {
        super();
        setHelp("What day it is ?");
    }

    @Override
    public void handleRequest(InternalRequest request, EngineResponse response, JSAPResult results) throws HokanException {
        LocalDateTime dateTime = LocalDateTime.now();
        ServiceResponse serviceResponse = doServicesRequest(ServiceRequestType.NIMIPAIVA_DAY_REQUEST, request.getIrcEvent(), dateTime);
        NimipaivaData names = serviceResponse.getNimipaivaDayResponse();
//        int week = LocalDateTime.now().get()

        LocalDate date = LocalDate.now();
        TemporalField woy = WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear();
        int weekNumber = date.get(woy);

        StringBuilder sb = new StringBuilder("Today is " + StringStuff.formatTime(TimeUtil.localDateTimeToDate(names.getDay()), StringStuff.STRING_STUFF_DF_DDMMYYYY) + " (W: " + weekNumber + ") :: ");
        for (int i = 0; i < names.getNames().size(); i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(names.getNames().get(i));
        }
        response.addResponse(sb.toString());
    }
}
