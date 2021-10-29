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
        helpGroups = {HelpGroup.SMS}
)
public class SMSCreditsCmd extends Cmd {

    public SMSCreditsCmd() {
        setHelp("Checks SMS credits available!");
    }

    @Override
    public void handleRequest(InternalRequest request, EngineResponse response, JSAPResult results) throws HokanException {

        ServiceResponse serviceResponse = doServicesRequest(ServiceRequestType.SMS_CREDIT_SERVICE_REQUEST, request.getIrcEvent(), (Object[]) null);
        String answer = serviceResponse.getSendSMSCreditResponse();
        if (answer != null) {
            response.addResponse("SMS credits: %s", answer);
        }

    }
}
