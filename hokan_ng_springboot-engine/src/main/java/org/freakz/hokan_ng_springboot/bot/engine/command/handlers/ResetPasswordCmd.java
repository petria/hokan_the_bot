package org.freakz.hokan_ng_springboot.bot.engine.command.handlers;

import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.UnflaggedOption;
import org.freakz.hokan_ng_springboot.bot.common.events.EngineResponse;
import org.freakz.hokan_ng_springboot.bot.common.events.InternalRequest;
import org.freakz.hokan_ng_springboot.bot.common.exception.HokanException;
import org.freakz.hokan_ng_springboot.bot.common.jpa.entity.User;
import org.freakz.hokan_ng_springboot.bot.common.util.StringStuff;
import org.freakz.hokan_ng_springboot.bot.engine.command.HelpGroup;
import org.freakz.hokan_ng_springboot.bot.engine.command.annotation.HelpGroups;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import static org.freakz.hokan_ng_springboot.bot.common.util.StaticStrings.ARG_NICK;

/**
 * Created by Petri Airio (petri.j.airio@gmail.com) on 10.9.2015.
 */
@Component
@Scope("prototype")

@HelpGroups(
        helpGroups = {HelpGroup.ACCESS_CONTROL}
)
public class ResetPasswordCmd extends Cmd {

    public ResetPasswordCmd() {
        super();
        setHelp("Reset user password to random string.");
        setAdminUserOnly(true);

        UnflaggedOption opt = new UnflaggedOption(ARG_NICK)
                .setRequired(false)
                .setGreedy(false);
        registerParameter(opt);

    }

    @Override
    public void handleRequest(InternalRequest request, EngineResponse response, JSAPResult results) throws HokanException {
        String target = results.getString(ARG_NICK, "me");
        User user;
        if (target.equals("me")) {
            user = request.getUser();
        } else {
            if (accessControlService.isAdminUser(request.getUser())) {
                user = userService.findFirstByNick(target);
            } else {
                response.addResponse("Only Admins can modify others data!");
                return;
            }
        }

        if (user == null) {
            response.addResponse("No PircBotUser found with: " + target);
            return;
        }

        String generated = StringStuff.generatePasswd(10);
        user.setPassword(StringStuff.getSHA1Password(generated));
        userService.save(user);
        response.addResponse("%s password reset to: %s", user.getNick(), generated);
    }

}
