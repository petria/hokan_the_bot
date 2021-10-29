package org.freakz.hokan_ng_springboot.bot.engine.command.handlers;

import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.UnflaggedOption;
import org.freakz.hokan_ng_springboot.bot.common.events.EngineResponse;
import org.freakz.hokan_ng_springboot.bot.common.events.InternalRequest;
import org.freakz.hokan_ng_springboot.bot.common.exception.HokanException;
import org.freakz.hokan_ng_springboot.bot.common.jpa.entity.Alias;
import org.freakz.hokan_ng_springboot.bot.engine.command.HelpGroup;
import org.freakz.hokan_ng_springboot.bot.engine.command.annotation.HelpGroups;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import static org.freakz.hokan_ng_springboot.bot.common.util.StaticStrings.ARG_ALIAS;

/**
 * User: petria
 * Date: 1/14/14
 * Time: 4:11 PM
 *
 * @author Petri Airio <petri.j.airio@gmail.com>
 */
@Component
@Scope("prototype")

@HelpGroups(
        helpGroups = {HelpGroup.ALIAS}
)
public class UnAliasCmd extends Cmd {


    public UnAliasCmd() {

        setHelp("Removes an alias.");

        UnflaggedOption flg = new UnflaggedOption(ARG_ALIAS)
                .setRequired(true)
                .setGreedy(false);
        registerParameter(flg);
    }

    @Override
    public void handleRequest(InternalRequest request, EngineResponse response, JSAPResult results) throws HokanException {
        Alias a = aliasService.findFirstByAlias(results.getString(ARG_ALIAS));
        if (a != null) {
            int r = aliasService.delete(a);
            if (r > 0) {
                response.addResponse("Alias removed, %3d: %s = %s", a.getAliasId(), a.getAlias(), a.getCommand());
            } else {
                response.addResponse("Alias not removed: %s", results.getString(ARG_ALIAS));
            }
        } else {
            response.addResponse("Unknown alias: %s", results.getString(ARG_ALIAS));
        }

    }

}
