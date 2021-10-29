package org.freakz.hokan_ng_springboot.bot.engine.command.handlers;

import com.martiansoftware.jsap.JSAPResult;
import org.freakz.hokan_ng_springboot.bot.common.events.EngineResponse;
import org.freakz.hokan_ng_springboot.bot.common.events.InternalRequest;
import org.freakz.hokan_ng_springboot.bot.common.events.ServiceRequestType;
import org.freakz.hokan_ng_springboot.bot.common.events.ServiceResponse;
import org.freakz.hokan_ng_springboot.bot.common.exception.HokanException;
import org.freakz.hokan_ng_springboot.bot.engine.command.HelpGroup;
import org.freakz.hokan_ng_springboot.bot.engine.command.annotation.HelpGroups;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
@HelpGroups(
        helpGroups = {HelpGroup.DATA_FETCHERS}
)
public class KoronaCmd extends Cmd {

    public KoronaCmd() {
        setHelp("Korona status!");
    }

    @Override
    public void handleRequest(InternalRequest request, EngineResponse response, JSAPResult results) throws HokanException {

        String nickId = request.getUser().getNick() + request.getUser().getId();
        ServiceResponse serviceResponse = doServicesRequest(ServiceRequestType.KORONA_REQUEST, request.getIrcEvent(), nickId);

        Integer[] n = serviceResponse.getKoronas();
        if (n != null) {
            String res =
                    String.format("Korona update - Dead : %d (+%d) - InIcu: %d (+%d) - InWard: %d (+%d) - Total hospitalised: %d (+%d)",
                            n[0], n[4],
                            n[1], n[5],
                            n[2], n[6],
                            n[3], n[7]
                    );
            response.addResponse("%s", res);
        } else {
            response.addResponse("Kaik kuallu?!");

        }
    }
}
