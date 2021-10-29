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

import static org.freakz.hokan_ng_springboot.bot.common.util.StaticStrings.ARG_RAWSTRING;

/**
 * User: petria
 * Date: 12/10/13
 * Time: 12:28 PM
 *
 * @author Petri Airio <petri.j.airio@gmail.com>
 */
@Component
@Scope("prototype")
@HelpGroups(
        helpGroups = {HelpGroup.SYSTEM}
)
public class RawCmd extends Cmd {

    public RawCmd() {
        super();
        setHelp("Sends raw command to the IRCd where this command was issued.");

        UnflaggedOption flg = new UnflaggedOption(ARG_RAWSTRING)
                .setRequired(true)
                .setGreedy(false);
        registerParameter(flg);

        setAdminUserOnly(true);
    }

    @Override
    public void handleRequest(InternalRequest request, EngineResponse response, JSAPResult results) throws HokanException {
        String raw = results.getString(ARG_RAWSTRING);
        response.addEngineMethodCall("sendRawLine", raw);
        response.addResponse("Sending: %s", raw);
    }

}
