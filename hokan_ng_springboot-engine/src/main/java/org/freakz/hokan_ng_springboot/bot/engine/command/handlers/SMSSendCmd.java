package org.freakz.hokan_ng_springboot.bot.engine.command.handlers;

import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.UnflaggedOption;
import org.freakz.hokan_ng_springboot.bot.common.events.*;
import org.freakz.hokan_ng_springboot.bot.common.exception.HokanException;
import org.freakz.hokan_ng_springboot.bot.common.jpa.entity.User;
import org.freakz.hokan_ng_springboot.bot.engine.command.HelpGroup;
import org.freakz.hokan_ng_springboot.bot.engine.command.annotation.HelpGroups;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import static org.freakz.hokan_ng_springboot.bot.common.util.StaticStrings.ARG_MESSAGE;
import static org.freakz.hokan_ng_springboot.bot.common.util.StaticStrings.ARG_NICK;

@Component
@Scope("prototype")
@HelpGroups(
        helpGroups = {HelpGroup.SMS}
)
public class SMSSendCmd extends Cmd {

    public SMSSendCmd() {
        setHelp("Sends SMS message if there is enough credits left!");

        UnflaggedOption unflaggedOption = new UnflaggedOption(ARG_NICK)
                .setRequired(true)
                .setGreedy(false);
        registerParameter(unflaggedOption);

        unflaggedOption = new UnflaggedOption(ARG_MESSAGE)
                .setRequired(true)
                .setGreedy(false);
        registerParameter(unflaggedOption);

        //setChannelOnly(true);
//        setChannelOpOnly(true);

    }

    @Override
    public void handleRequest(InternalRequest request, EngineResponse response, JSAPResult results) throws HokanException {
        String nick = results.getString(ARG_NICK);
        String message = results.getString(ARG_MESSAGE);
        if (!accessControlService.isAdminUser(request.getUser())) {
            if (request.getUser().getPhone() == null || request.getUser().getPhone().isEmpty()) {
                response.setReplyTo(request.getUser().getNick());
                response.addResponse("Before sending SMS your own number must be set. Use command: !usermod -v -p <num> to do it!\n");
                return;
            }
        }


        User hUser;
        hUser = userService.findFirstByNick(nick);
        if (hUser == null) {
            response.addResponse("Unknown nick: %s", nick);
            return;
        }
        if (hUser.getPhone() == null || hUser.getPhone().isEmpty()) {
            response.addResponse("User %s phone number not set", hUser.getNick());
            return;
        }
        SendSMSRequest smsRequest = new SendSMSRequest();
        smsRequest.setTarget(hUser.getPhone());
        smsRequest.setMessage(message);

        ServiceResponse serviceResponse = doServicesRequest(ServiceRequestType.SMS_SEND_SERVICE_REQUEST, request.getIrcEvent(), smsRequest);
        SendSMSResponse answer = serviceResponse.getSendSMSResponse();
        if (answer.getStatus().equals("OK")) {
            response.addResponse("SMS sent cost: %s€, credits left: %s€", answer.getCost(), answer.getCredits());
        } else {
            response.addResponse("SMS sent failed: %s %s", answer.getErrorCode(), answer.getErrorReason());
        }

    }
}
