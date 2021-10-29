package org.freakz.hokan_ng_springboot.bot.engine.command.handlers;

import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.UnflaggedOption;
import org.freakz.hokan_ng_springboot.bot.common.events.EngineResponse;
import org.freakz.hokan_ng_springboot.bot.common.events.InternalRequest;
import org.freakz.hokan_ng_springboot.bot.common.events.ServiceRequestType;
import org.freakz.hokan_ng_springboot.bot.common.events.ServiceResponse;
import org.freakz.hokan_ng_springboot.bot.common.exception.HokanException;
import org.freakz.hokan_ng_springboot.bot.common.models.IMDBSearchResults;
import org.freakz.hokan_ng_springboot.bot.engine.command.HelpGroup;
import org.freakz.hokan_ng_springboot.bot.engine.command.annotation.HelpGroups;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import static org.freakz.hokan_ng_springboot.bot.common.util.StaticStrings.ARG_TEXT;

/**
 * Created by Petri Airio on 18.11.2015.
 * -
 */
@Component
@Scope("prototype")

@HelpGroups(
        helpGroups = {HelpGroup.DATA_FETCHERS}
)
public class IMDBFindCmd extends Cmd {

    public IMDBFindCmd() {

        setHelp("Queries IMDB database using title search.");

        UnflaggedOption flg = new UnflaggedOption(ARG_TEXT)
                .setRequired(true)
                .setGreedy(false);
        registerParameter(flg);

        setBroken(true); // TODO FIX

    }


    @Override
    public void handleRequest(InternalRequest request, EngineResponse response, JSAPResult results) throws HokanException {
        String text = results.getString(ARG_TEXT);
        ServiceResponse serviceResponse = doServicesRequest(ServiceRequestType.IMDB_TITLE_REQUEST, request.getIrcEvent(), text);
        IMDBSearchResults imdbSearchResults = serviceResponse.getIMDBTitleData();
/*        if (imdbSearchResults.getSearchResults() != null) {
            for (OmdbVideoBasic omdb : imdbSearchResults.getSearchResults()) {
                String imdbURL = String.format("http://www.imdb.com/title/%s/", omdb.getImdbID());
                response.addResponse("[%7s] %25s :: \"%s\" (%s)\n", omdb.getType(), imdbURL, omdb.getTitle(), omdb.getYear());
            }
        } else {
            response.addResponse("Nothing found with: %s", text);
        }
        TODO
        */
    }
}
