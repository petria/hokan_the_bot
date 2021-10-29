package org.freakz.hokan_ng_springboot.bot.engine.command.handlers;

import com.martiansoftware.jsap.JSAPResult;
import org.freakz.hokan_ng_springboot.bot.common.enums.LunchPlace;
import org.freakz.hokan_ng_springboot.bot.common.events.EngineResponse;
import org.freakz.hokan_ng_springboot.bot.common.events.InternalRequest;
import org.freakz.hokan_ng_springboot.bot.common.events.ServiceRequestType;
import org.freakz.hokan_ng_springboot.bot.common.events.ServiceResponse;
import org.freakz.hokan_ng_springboot.bot.common.exception.HokanException;
import org.freakz.hokan_ng_springboot.bot.engine.command.HelpGroup;
import org.freakz.hokan_ng_springboot.bot.engine.command.annotation.HelpGroups;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by Petri Airio on 27.1.2016.
 * -
 */
@Component
@Scope("prototype")

@HelpGroups(
        helpGroups = {HelpGroup.DATA_FETCHERS, HelpGroup.LUNCH}
)
public class LunchPlacesCmd extends Cmd {

    public LunchPlacesCmd() {
        super();
        setHelp("Shows available Lunch places where menu lists are fetched.");
    }

    @Override
    public void handleRequest(InternalRequest request, EngineResponse response, JSAPResult results) throws HokanException {
        ServiceResponse serviceResponse = doServicesRequest(ServiceRequestType.LUNCH_PLACES_REQUEST, request.getIrcEvent(), "");
        List<LunchPlace> lunchPlaces = serviceResponse.getLunchPlacesResponse();
        if (lunchPlaces.size() == 0) {
            response.addResponse("No lunch places!!");
        } else {
            String places = "I know following lunch places:";
            for (LunchPlace lunchPlace : lunchPlaces) {
                places += "  '" + lunchPlace.getName() + "'";
            }
            response.addResponse("%s", places);
        }
    }

}
