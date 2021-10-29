package org.freakz.hokan_ng_springboot.bot.engine.command.handlers;

import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.UnflaggedOption;
import org.freakz.hokan_ng_springboot.bot.common.events.EngineResponse;
import org.freakz.hokan_ng_springboot.bot.common.events.InternalRequest;
import org.freakz.hokan_ng_springboot.bot.common.events.ServiceRequestType;
import org.freakz.hokan_ng_springboot.bot.common.events.ServiceResponse;
import org.freakz.hokan_ng_springboot.bot.common.exception.HokanException;
import org.freakz.hokan_ng_springboot.bot.common.models.AlkoSearchResults;
import org.freakz.hokan_ng_springboot.bot.engine.command.HelpGroup;
import org.freakz.hokan_ng_springboot.bot.engine.command.annotation.HelpGroups;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import static org.freakz.hokan_ng_springboot.bot.common.util.StaticStrings.ARG_SEARCH;

/**
 * Created by Petri Airio (petri.j.airio@gmail.com) 22/11/2016 / 9.03
 */
@Component
@Scope("prototype")

@HelpGroups(
        helpGroups = {HelpGroup.DATA_FETCHERS}
)
public class AlkoSearchCmd extends Cmd {

    public AlkoSearchCmd() {
        setHelp("Search booze from alko.fi ");

        UnflaggedOption flg = new UnflaggedOption(ARG_SEARCH)
                .setRequired(true)
                .setGreedy(false);
        registerParameter(flg);

    }

    @Override
    public void handleRequest(InternalRequest request, EngineResponse response, JSAPResult results) throws HokanException {
        String text = results.getString(ARG_SEARCH);
        ServiceResponse serviceResponse = doServicesRequest(ServiceRequestType.ALKO_SEARCH_REQUEST, request.getIrcEvent(), text);
        AlkoSearchResults alkoSearchResults = serviceResponse.getAlkoSearchResults();
        if (alkoSearchResults.getResults().size() > 0) {
            String resp = "";
            for (int i = 0; i < alkoSearchResults.getResults().size(); i++) {
                resp += (i + 1) + ") " + alkoSearchResults.getResults().get(i) + " ";
            }
            response.addResponse(resp);
        } else {
            response.addResponse("No booze found with: %s", text);
        }
    }

}
