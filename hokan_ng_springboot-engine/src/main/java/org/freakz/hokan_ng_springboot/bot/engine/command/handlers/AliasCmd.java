package org.freakz.hokan_ng_springboot.bot.engine.command.handlers;

import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.UnflaggedOption;
import org.freakz.hokan_ng_springboot.bot.common.events.EngineResponse;
import org.freakz.hokan_ng_springboot.bot.common.events.InternalRequest;
import org.freakz.hokan_ng_springboot.bot.common.exception.HokanException;
import org.freakz.hokan_ng_springboot.bot.common.jpa.entity.Alias;
import org.freakz.hokan_ng_springboot.bot.common.jpa.entity.AliasType;
import org.freakz.hokan_ng_springboot.bot.engine.command.HelpGroup;
import org.freakz.hokan_ng_springboot.bot.engine.command.annotation.HelpGroups;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

import static org.freakz.hokan_ng_springboot.bot.common.util.StaticStrings.ARG_ALIAS;
import static org.freakz.hokan_ng_springboot.bot.common.util.StaticStrings.ARG_COMMAND;

/**
 * User: petria
 * Date: 1/14/14
 * Time: 3:41 PM
 *
 * @author Petri Airio <petri.j.airio@gmail.com>
 */
@Component
@Scope("prototype")

@HelpGroups(
        helpGroups = {HelpGroup.ALIAS}
)
public class AliasCmd extends Cmd {

    public AliasCmd() {
        super();
        setHelp("Lists and sets aliases.");

        UnflaggedOption flg = new UnflaggedOption(ARG_ALIAS)
                .setRequired(false)
                .setGreedy(false);
        registerParameter(flg);

        flg = new UnflaggedOption(ARG_COMMAND)
                .setRequired(false)
                .setGreedy(false);
        registerParameter(flg);

    }

    @Override
    public void handleRequest(InternalRequest request, EngineResponse response, JSAPResult results) throws HokanException {
        if (null == results.getString(ARG_ALIAS)) {
            List<Alias> aliases = aliasService.findAll();
            if (aliases.size() > 0) {
                for (Alias alias : aliases) {
                    response.addResponse("%3d: %s = %s\n", alias.getAliasId(), alias.getAlias(), alias.getCommand());
                }
            } else {
                response.addResponse("No aliases defined!");
            }
        } else {
            String alias = results.getString(ARG_ALIAS);
            if (alias == null) {
                response.addResponse("Alias key can't be null, usage: !alias <key> <command>");
                return;
            }
            String command = results.getString(ARG_COMMAND);
            if (command == null) {
                response.addResponse("Alias command can't be null, usage: !alias <key> <command>");
                return;
            }
            Alias a = aliasService.findFirstByAlias(alias);
            if (a == null) {
                a = new Alias();
                a.setCreatedBy(request.getUser().getNick());
                a.setCreated(new Date());
                a.setAliasType(AliasType.ALIAS_PUBLIC);
                a.setAlias(alias);
            }
            a.setCommand(command);
            a = aliasService.save(a);
            response.addResponse("Alias set, %3d: %s = %s", a.getAliasId(), a.getAlias(), a.getCommand());
        }
    }

}
