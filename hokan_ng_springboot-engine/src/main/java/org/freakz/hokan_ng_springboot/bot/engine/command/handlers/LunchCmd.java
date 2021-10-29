package org.freakz.hokan_ng_springboot.bot.engine.command.handlers;

import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.UnflaggedOption;
import org.freakz.hokan_ng_springboot.bot.common.enums.LunchDay;
import org.freakz.hokan_ng_springboot.bot.common.enums.LunchPlace;
import org.freakz.hokan_ng_springboot.bot.common.events.EngineResponse;
import org.freakz.hokan_ng_springboot.bot.common.events.InternalRequest;
import org.freakz.hokan_ng_springboot.bot.common.events.ServiceRequestType;
import org.freakz.hokan_ng_springboot.bot.common.events.ServiceResponse;
import org.freakz.hokan_ng_springboot.bot.common.exception.HokanException;
import org.freakz.hokan_ng_springboot.bot.common.models.LunchMenu;
import org.freakz.hokan_ng_springboot.bot.common.models.LunchPlaceData;
import org.freakz.hokan_ng_springboot.bot.common.util.StringStuff;
import org.freakz.hokan_ng_springboot.bot.common.util.TimeUtil;
import org.freakz.hokan_ng_springboot.bot.engine.command.HelpGroup;
import org.freakz.hokan_ng_springboot.bot.engine.command.annotation.HelpGroups;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Set;

import static org.freakz.hokan_ng_springboot.bot.common.util.StaticStrings.ARG_LUNCH_PLACE;

/**
 * Created by Petri Airio on 27.1.2016.
 * -
 */
@Component
@Scope("prototype")

@HelpGroups(
        helpGroups = {HelpGroup.DATA_FETCHERS, HelpGroup.LUNCH}
)
public class LunchCmd extends Cmd {


    public LunchCmd() {
        super();
        setHelp("Shows lunch menu of selected place.");

        UnflaggedOption unflaggedOption = new UnflaggedOption(ARG_LUNCH_PLACE)
                .setRequired(true)
                .setGreedy(false);
        registerParameter(unflaggedOption);

    }

    @Override
    public void handleRequest(InternalRequest request, EngineResponse response, JSAPResult results) throws HokanException {
        String argLunchPlace = results.getString(ARG_LUNCH_PLACE);
//        LunchPlace place = LunchPlace.getLunchPlace(argLunchPlace);
        Set<LunchPlace> matchingLunchPlaces = LunchPlace.getMatchingLunchPlaces(argLunchPlace);
        if (matchingLunchPlaces.size() == 0) {
            String places = "I know following lunch places: ";
            for (LunchPlace lunchPlace : LunchPlace.values()) {
                places += "  " + lunchPlace.getName();
            }

            response.addResponse("Unknown lunch place: %s\n%s", argLunchPlace, places);
            return;
        }
        LocalDateTime day = LocalDateTime.now();
        ServiceResponse serviceResponse = doServicesRequest(ServiceRequestType.LUNCH_REQUEST, request.getIrcEvent(), matchingLunchPlaces, day);
        LunchPlaceData lunchPlaceData = serviceResponse.getLunchResponse();
        if (lunchPlaceData == null || lunchPlaceData.getLunchDataMap().size() == 0) {
            response.addResponse("n/a");
            return;
        }
        LunchDay lunchDay = LunchDay.getFromDateTime(day);
        for (LunchPlace place : matchingLunchPlaces) {
            LunchMenu lunchMenu = lunchPlaceData.getLunchDataMap().get(place).getMenu().get(lunchDay);
            String menuText;
            if (lunchMenu == null) {
                menuText = "n/a";
            } else {
                menuText = lunchMenu.getMenu();
            }
            String dayStr = StringStuff.formatTime(TimeUtil.localDateTimeToDate(day), StringStuff.STRING_STUFF_DF_DM);
            response.addResponse("%s %s - %s\n", dayStr, place.getName(), menuText);

        }
    }

}
