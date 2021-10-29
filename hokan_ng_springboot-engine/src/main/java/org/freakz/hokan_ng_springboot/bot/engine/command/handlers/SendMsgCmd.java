package org.freakz.hokan_ng_springboot.bot.engine.command.handlers;

import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.UnflaggedOption;
import org.freakz.hokan_ng_springboot.bot.common.events.EngineResponse;
import org.freakz.hokan_ng_springboot.bot.common.events.InternalRequest;
import org.freakz.hokan_ng_springboot.bot.common.exception.HokanException;
import org.freakz.hokan_ng_springboot.bot.engine.command.HelpGroup;
import org.freakz.hokan_ng_springboot.bot.engine.command.annotation.HelpGroups;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import static org.freakz.hokan_ng_springboot.bot.common.util.StaticStrings.ARG_MESSAGE;
import static org.freakz.hokan_ng_springboot.bot.common.util.StaticStrings.ARG_TARGET;

/**
 * Created by Petri Airio on 6.11.2015.
 */
@Component
@Scope("prototype")

@HelpGroups(
        helpGroups = {HelpGroup.ADMIN}
)
public class SendMsgCmd extends Cmd {

    public SendMsgCmd() {

        setHelp("Try to send message to target user or channel.");

        UnflaggedOption unflaggedOption = new UnflaggedOption(ARG_TARGET)
                .setRequired(true)
                .setGreedy(false);
        registerParameter(unflaggedOption);

        unflaggedOption = new UnflaggedOption(ARG_MESSAGE)
                .setRequired(true)
                .setGreedy(false);
        registerParameter(unflaggedOption);

        setAdminUserOnly(true);

    }

    @Override
    public void handleRequest(InternalRequest request, EngineResponse response, JSAPResult results) throws HokanException {

        String target = results.getString(ARG_TARGET);
        String message = results.getString(ARG_MESSAGE);
        response.setReplyTo(target);
        response.addResponse("%s", message);

    }
}
