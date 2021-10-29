package org.freakz.hokan_ng_springboot.bot.engine.command.handlers;

import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.UnflaggedOption;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.freakz.hokan_ng_springboot.bot.common.util.StaticStrings.ARG_NIMI_OR_PVM;

/**
 * User: petria
 * Date: 1/13/14
 * Time: 12:36 PM
 *
 * @author Petri Airio <petri.j.airio@gmail.com>
 */
@Component
@Scope("prototype")

public class NimipaivaCmd extends Cmd {

    private static final String NIMIPAIVAT_TXT = "/Nimipaivat.txt";
    private static final int PVM_MODE = 0;
    private static final int NAME_MODE = 1;

    private List<String> nimiPvmList = new ArrayList<>();

    public NimipaivaCmd() {
        super();
        setHelp("Nimipäivät");

        UnflaggedOption flg = new UnflaggedOption(ARG_NIMI_OR_PVM)
                .setRequired(false)
                .setGreedy(false);
        registerParameter(flg);

    }

    @Override
    public void handleRequest(InternalRequest request, EngineResponse response, JSAPResult results) throws HokanException {

        String nimiOrPvm = results.getString(ARG_NIMI_OR_PVM);
        LocalDateTime dateTime;
        if (nimiOrPvm == null) {
            dateTime = LocalDateTime.now();
        } else {
            dateTime = TimeUtil.parseDateTime(nimiOrPvm);
        }
        if (dateTime != null) {
            ServiceResponse serviceResponse = doServicesRequest(ServiceRequestType.NIMIPAIVA_DAY_REQUEST, request.getIrcEvent(), dateTime);
            NimipaivaData names = serviceResponse.getNimipaivaDayResponse();
            if (names.getNames().size() > 0) {
                StringBuilder sb = new StringBuilder(StringStuff.formatTime(TimeUtil.localDateTimeToDate(names.getDay()), StringStuff.STRING_STUFF_DF_DDMMYYYY) + " ::");
                for (String name : names.getNames()) {
                    sb.append(" ").append(name);
                }
                response.addResponse("%s", sb.toString());
            }
        } else {
            ServiceResponse serviceResponse = doServicesRequest(ServiceRequestType.NIMIPAIVA_NAME_REQUEST, request.getIrcEvent(), nimiOrPvm);
            NimipaivaData names = serviceResponse.getNimipaivaNameResponse();
            if (names != null) {

                StringBuilder sb = new StringBuilder(StringStuff.formatTime(TimeUtil.localDateTimeToDate(names.getDay()), StringStuff.STRING_STUFF_DF_DDMMYYYY) + " ::");
                for (String name : names.getNames()) {
                    sb.append(" ").append(name);
                }
                response.addResponse("%s", sb.toString());
            } else {
                response.addResponse("n/a");
            }
        }

    }

}
