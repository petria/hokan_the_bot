package org.freakz.hokan_ng_springboot.bot.engine.command.handlers;

import com.martiansoftware.jsap.JSAPResult;
import org.freakz.hokan_ng_springboot.bot.common.events.EngineResponse;
import org.freakz.hokan_ng_springboot.bot.common.events.InternalRequest;
import org.freakz.hokan_ng_springboot.bot.common.events.ServiceRequestType;
import org.freakz.hokan_ng_springboot.bot.common.events.ServiceResponse;
import org.freakz.hokan_ng_springboot.bot.common.exception.HokanException;
import org.freakz.hokan_ng_springboot.bot.common.models.NatoRatifyStats;
import org.freakz.hokan_ng_springboot.bot.engine.command.HelpGroup;
import org.freakz.hokan_ng_springboot.bot.engine.command.annotation.HelpGroups;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@HelpGroups(
        helpGroups = {HelpGroup.DATA_FETCHERS}
)
@Scope("prototype")
public class NatoCmd extends Cmd {

    @Override
    public void handleRequest(InternalRequest request, EngineResponse response, JSAPResult results) throws HokanException {
        ServiceResponse serviceResponse = doServicesRequest(ServiceRequestType.NATO_REQUEST, request.getIrcEvent(), "");
        if (serviceResponse != null) {
            NatoRatifyStats ratifyStats = serviceResponse.getNatoRatifyStats();
            response.addResponse("OTAN: %s", ratifyStats.getSummary());
        }

    }
}
