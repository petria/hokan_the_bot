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

import static org.freakz.hokan_ng_springboot.bot.common.util.StaticStrings.ARG_UPDATER_NAME;

/**
 * Created by Petri Airio on 16.5.2016.
 * -
 */
@Component
@Scope("prototype")
@HelpGroups(
        helpGroups = {HelpGroup.UPDATERS}
)
public class UpdaterInfoCmd extends Cmd {

    public UpdaterInfoCmd() {

        UnflaggedOption unflaggedOption = new UnflaggedOption(ARG_UPDATER_NAME)
                .setRequired(true)
                .setGreedy(false);
        registerParameter(unflaggedOption);
        setHelp("Show details about data Updaters. Use !updaterlist to see available data Updaters.");

    }

    @Override
    public void handleRequest(InternalRequest request, EngineResponse response, JSAPResult results) throws HokanException {
        String updateName = results.getString(ARG_UPDATER_NAME);
        ServiceResponse serviceResponse = doServicesRequest(ServiceRequestType.UPDATERS_LIST_REQUEST, request.getIrcEvent(), ".*");
        List<DataUpdaterModel> modelList = serviceResponse.getUpdaterListData();
        boolean found = false;
        String txt = "";
        for (DataUpdaterModel model : modelList) {
            if (model.getName().matches(updateName)) {
                txt += String.format("Name                : %s\n", model.getName());
                txt += String.format("Last update         : %s\n", model.getLastUpdate() + "");
                txt += String.format("Next update         : %s\n", model.getNextUpdate() + "");
                txt += String.format("Last update runtime : %s\n", model.getLastUpdateRuntime());
                txt += String.format("Total runtime       : %s", model.getTotalUpdateRuntime());
                found = true;
                break;
            }
        }
        if (found) {
            response.addResponse("%s", txt);
        } else {
            response.addResponse("Not a valid Updater name: %s. Use !updaterlist to see available Updaters.", updateName);
        }

    }
}
