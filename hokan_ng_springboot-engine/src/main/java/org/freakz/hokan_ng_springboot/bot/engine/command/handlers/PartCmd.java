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

import static org.freakz.hokan_ng_springboot.bot.common.util.StaticStrings.ARG_CHANNEL;

/**
 * User: petria
 * Date: 11/8/13
 * Time: 3:46 PM
 *
 * @author Petri Airio <petri.j.airio@gmail.com>
 */
@Component
@Scope("prototype")
@HelpGroups(
        helpGroups = {HelpGroup.CHANNELS}
)
public class PartCmd extends Cmd {

    public PartCmd() {
        super();

        UnflaggedOption uflg = new UnflaggedOption(ARG_CHANNEL)
                .setRequired(true)
                .setGreedy(false);
        registerParameter(uflg);

        setAdminUserOnly(true);
    }

/*  @Override
  public String getMatchPattern() {
    return "!part.*";
  }*/

    @Override
    public void handleRequest(InternalRequest request, EngineResponse response, JSAPResult results) throws HokanException {
        String channel = results.getString(ARG_CHANNEL);
        response.setResponseMessage("Leaving: " + channel);
        response.addEngineMethodCall("partChannel", channel);
    }

}
