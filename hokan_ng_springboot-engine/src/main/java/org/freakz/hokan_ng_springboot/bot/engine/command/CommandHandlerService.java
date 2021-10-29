package org.freakz.hokan_ng_springboot.bot.engine.command;

import org.freakz.hokan_ng_springboot.bot.engine.command.handlers.Cmd;
import org.freakz.hokan_ng_springboot.bot.engine.service.CmdHandlerMatches;

import java.util.List;

/**
 * User: petria
 * Date: 11/6/13
 * Time: 4:08 PM
 *
 * @author Petri Airio <petri.j.airio@gmail.com>
 */
public interface CommandHandlerService {

    Cmd getCommandHandler(String line);

    //    @PreAuthorize("hasRole('ADMIN')")
    CmdHandlerMatches getMatchingCommands(String line);

    List<Cmd> getCommandHandlers();

    List<Cmd> getCommandHandlersByName(String name);

}
