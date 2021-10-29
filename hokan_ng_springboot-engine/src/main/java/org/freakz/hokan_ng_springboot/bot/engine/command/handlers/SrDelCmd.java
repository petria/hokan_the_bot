package org.freakz.hokan_ng_springboot.bot.engine.command.handlers;

import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.UnflaggedOption;
import org.freakz.hokan_ng_springboot.bot.common.events.EngineResponse;
import org.freakz.hokan_ng_springboot.bot.common.events.InternalRequest;
import org.freakz.hokan_ng_springboot.bot.common.exception.HokanException;
import org.freakz.hokan_ng_springboot.bot.common.jpa.entity.SearchReplace;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.freakz.hokan_ng_springboot.bot.common.util.StaticStrings.ARG_ID_OR_SEARCH;

/**
 * Created by pairio on 31.5.2014.
 *
 * @author Petri Airio (petri.j.airio@gmail.com)
 */
@Component
@Scope("prototype")

public class SrDelCmd extends Cmd {

    public SrDelCmd() {
        super();
        setHelp("Deletes Search/Replace by id or search keyword.");

        UnflaggedOption opt = new UnflaggedOption(ARG_ID_OR_SEARCH)
                .setRequired(true)
                .setGreedy(false);
        registerParameter(opt);

    }

    @Override
    public void handleRequest(InternalRequest request, EngineResponse response, JSAPResult results) throws HokanException {
        String idORSearch = results.getString(ARG_ID_OR_SEARCH);
        long id = 0;
        try {
            id = Long.parseLong(idORSearch);
        } catch (NumberFormatException nfe) {
            id = -1;
        }
        if (id == -1) {

            List<SearchReplace> srList = searchReplaceService.findByTheSearch(idORSearch);

            if (srList.size() > 0) {
                for (SearchReplace sr : srList) {
                    searchReplaceService.delete(sr);
                    response.addResponse("Removed Search/Replace: [%2d] s/%s/%s/", sr.getId(), sr.getSearch(), sr.getReplace());
                }
            } else {
                response.addResponse("No matching Search/Replaces found with: %s", idORSearch);
            }

        } else {
            SearchReplace sr = searchReplaceService.findOne(id);
            if (sr != null) {
                searchReplaceService.delete(sr);
                response.addResponse("Removed Search/Replace: [%2d] s/%s/%s/", sr.getId(), sr.getSearch(), sr.getReplace());
            } else {
                response.addResponse("Search/Replace not found with id: {}", id);
            }
        }
    }

}
