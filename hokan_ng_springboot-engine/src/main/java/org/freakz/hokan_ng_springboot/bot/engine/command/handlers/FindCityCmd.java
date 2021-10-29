package org.freakz.hokan_ng_springboot.bot.engine.command.handlers;

import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.UnflaggedOption;
import org.freakz.hokan_ng_springboot.bot.common.events.EngineResponse;
import org.freakz.hokan_ng_springboot.bot.common.events.InternalRequest;
import org.freakz.hokan_ng_springboot.bot.common.events.ServiceRequestType;
import org.freakz.hokan_ng_springboot.bot.common.events.ServiceResponse;
import org.freakz.hokan_ng_springboot.bot.common.exception.HokanException;
import org.freakz.hokan_ng_springboot.bot.common.models.FindCityResults;
import org.freakz.hokan_ng_springboot.bot.engine.command.HelpGroup;
import org.freakz.hokan_ng_springboot.bot.engine.command.annotation.HelpGroups;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import static org.freakz.hokan_ng_springboot.bot.common.util.StaticStrings.ARG_CITY;

@Component
@Scope("prototype")

@HelpGroups(
        helpGroups = {HelpGroup.DATA_FETCHERS}
)
public class FindCityCmd extends Cmd {

    public FindCityCmd() {
        super();
        setHelp("Finds city from World City Data.");

        UnflaggedOption flg = new UnflaggedOption(ARG_CITY)
                .setRequired(true)
                .setGreedy(false);
        registerParameter(flg);

    }

    @Override
    public void handleRequest(InternalRequest request, EngineResponse response, JSAPResult results) throws HokanException {
        String cityArg = results.getString(ARG_CITY).toLowerCase();
        ServiceResponse serviceResponse = doServicesRequest(ServiceRequestType.FIND_CITY_REQUEST, request.getIrcEvent(), cityArg);
        final FindCityResults findCityResults = serviceResponse.getFindCityResults();
        if (findCityResults.getWorldCityDataList().size() > 0) {
            response.setResponseMessage("found: " + findCityResults.getWorldCityDataList().size());
        } else {
            response.setResponseMessage("No cities found with: " + cityArg);
        }
    }
}
