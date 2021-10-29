package org.freakz.hokan_ng_springboot.bot.engine.command.handlers;

import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.UnflaggedOption;
import org.freakz.hokan_ng_springboot.bot.common.events.EngineResponse;
import org.freakz.hokan_ng_springboot.bot.common.events.InternalRequest;
import org.freakz.hokan_ng_springboot.bot.common.events.ServiceRequestType;
import org.freakz.hokan_ng_springboot.bot.common.events.ServiceResponse;
import org.freakz.hokan_ng_springboot.bot.common.exception.HokanException;
import org.freakz.hokan_ng_springboot.bot.common.models.DataUpdaterModel;
import org.freakz.hokan_ng_springboot.bot.engine.command.HelpGroup;
import org.freakz.hokan_ng_springboot.bot.engine.command.annotation.HelpGroups;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.freakz.hokan_ng_springboot.bot.common.util.StaticStrings.ARG_UPDATER;

/**
 * Created by Petri Airio on 17.6.2015.
 */
@Component
@Scope("prototype")

@HelpGroups(
        helpGroups = {HelpGroup.UPDATERS}
)
public class UpdaterStartCmd extends Cmd {

    public UpdaterStartCmd() {
        super();
        setHelp("Starts specific updater.");

        UnflaggedOption opt = new UnflaggedOption(ARG_UPDATER)
                .setRequired(true)
                .setGreedy(false);
        registerParameter(opt);

        setAdminUserOnly(true);
    }

    @Override
    public void handleRequest(InternalRequest request, EngineResponse response, JSAPResult results) throws HokanException {
        String updater = results.getString(ARG_UPDATER);
        ServiceResponse serviceResponse = doServicesRequest(ServiceRequestType.UPDATERS_START_REQUEST, request.getIrcEvent(), updater);
        List<DataUpdaterModel> modelList = serviceResponse.getStartUpdaterListData();
        if (modelList.size() > 0) {
            response.addResponse("Started following updaters:");
            for (DataUpdaterModel model : modelList) {
                String txt = String.format("%15s\n", model.getName());
                response.addResponse(txt);
            }
        } else {
            response.addResponse("No updaters started!");
        }

    }
}
