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

import java.util.List;

@Component
@Scope("prototype")
@HelpGroups(
        helpGroups = {HelpGroup.SMS}
)
public class KoronaNotifyCmd extends Cmd {

    public KoronaNotifyCmd() {
        setHelp("Trigger Korona notify broadcast");
        setAdminUserOnly(true);
    }


    @Override
    public void handleRequest(InternalRequest request, EngineResponse response, JSAPResult results) throws HokanException {
        ServiceResponse serviceResponse = doServicesRequest(ServiceRequestType.KORONA_NOTIFY_REQUEST, request.getIrcEvent(), null);
        Object responseData = serviceResponse.getResponseData(ServiceRequestType.KORONA_NOTIFY_REQUEST.getResponseDataKey());
        if (responseData != null) {
            String ret = "Korona notify broadcast triggered: ";
            List<String> list = (List<String>) responseData;
            for (String channel : list) {
                ret += channel + "  ";
            }
            response.addResponse(ret);
        }
    }
}
