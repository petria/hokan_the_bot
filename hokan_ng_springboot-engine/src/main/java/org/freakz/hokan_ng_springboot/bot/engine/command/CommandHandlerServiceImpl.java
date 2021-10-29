package org.freakz.hokan_ng_springboot.bot.engine.command;

import org.freakz.hokan_ng_springboot.bot.engine.command.handlers.Cmd;
import org.freakz.hokan_ng_springboot.bot.engine.service.CmdHandlerMatches;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * User: petria
 * Date: 11/6/13
 * Time: 4:00 PM
 *
 * @author Petri Airio <petri.j.airio@gmail.com>
 */
@Service
public class CommandHandlerServiceImpl implements CommandHandlerService {

    private final ApplicationContext context;

    @Autowired
    public CommandHandlerServiceImpl(ApplicationContext context) {
        this.context = context;
    }

    @Override
    public CmdHandlerMatches getMatchingCommands(String line) {
        String firstWord = line.split(" ")[0].toLowerCase();

        List<Cmd> matches = new ArrayList<>();
        for (Cmd base : getCommandHandlers()) {
            String baseMatch = base.getMatchPattern();
            if (baseMatch.startsWith(firstWord)) {
                matches.add(base);
            }
        }

        CmdHandlerMatches cmdHandlerMatches = new CmdHandlerMatches(firstWord);
        if (matches.size() == 1) {
            cmdHandlerMatches.getMatches().add(context.getBean(matches.get(0).getClass()));
        } else if (matches.size() > 1) {
            for (Cmd base : matches) {
                String baseMatch = base.getMatchPattern();
                if (firstWord.equals(baseMatch)) {
                    cmdHandlerMatches.getMatches().add(context.getBean(base.getClass()));
                    return cmdHandlerMatches;
                }
            }
            cmdHandlerMatches.getMatches().addAll(matches);
        }
        return cmdHandlerMatches;
    }

    @Override
    public Cmd getCommandHandler(String line) {
        /* Deprecated */
        return null;
    }

    @Override
    public List<Cmd> getCommandHandlers() {
        return new ArrayList<>(context.getBeansOfType(Cmd.class).values());
    }

    @Override
    public List<Cmd> getCommandHandlersByName(String name) {
        List<Cmd> matches = new ArrayList<>();
        for (Cmd cmd : getCommandHandlers()) {
            if (cmd.getName().toLowerCase().startsWith(name.toLowerCase())) {
                matches.add(cmd);
            }
        }
        return matches;
    }
}
