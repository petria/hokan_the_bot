package org.freakz.hokan_ng_springboot.bot.engine.command.handlers;

import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.UnflaggedOption;
import org.freakz.hokan_ng_springboot.bot.common.events.EngineResponse;
import org.freakz.hokan_ng_springboot.bot.common.events.InternalRequest;
import org.freakz.hokan_ng_springboot.bot.common.exception.HokanException;
import org.freakz.hokan_ng_springboot.bot.common.jpa.entity.Url;
import org.freakz.hokan_ng_springboot.bot.common.util.StringStuff;
import org.freakz.hokan_ng_springboot.bot.engine.command.HelpGroup;
import org.freakz.hokan_ng_springboot.bot.engine.command.annotation.HelpGroups;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.freakz.hokan_ng_springboot.bot.common.util.StaticStrings.ARG_NICK;
import static org.freakz.hokan_ng_springboot.bot.common.util.StaticStrings.ARG_URL_PATTERN;

/**
 * User: petria
 * Date: 12/14/13
 * Time: 12:14 PM
 *
 * @author Petri Airio <petri.j.airio@gmail.com>
 */
@Component
@Scope("prototype")

@HelpGroups(
        helpGroups = {HelpGroup.URLS}
)
public class FindUrlCmd extends Cmd {

    public FindUrlCmd() {
        super();
        setHelp("Finds matching urls from URL database.");

        UnflaggedOption flg = new UnflaggedOption(ARG_URL_PATTERN)
                .setRequired(true)
                .setGreedy(false);
        registerParameter(flg);

        flg = new UnflaggedOption(ARG_NICK)
                .setRequired(false)
                .setGreedy(false);
        registerParameter(flg);

    }

    @Override
    public String getExample() {
        return "!findurl psytrance dezahn";
    }

    @Override
    public void handleRequest(InternalRequest request, EngineResponse response, JSAPResult results) throws HokanException {
        String urlPattern = results.getString(ARG_URL_PATTERN);
        String nick = results.getString(ARG_NICK);
        List<Url> urlList;
        if (nick == null) {
            urlList = urlLoggerService.findByUrl(urlPattern);
        } else {
            urlList = urlLoggerService.findByUrlAndNicks(urlPattern, nick);
        }

        int shown = 0;
        String ret = null;

        for (Url row : urlList) {

            if (ret == null) {
                ret = "";
            }

            if (shown > 0) {
                ret += " ";
            }

            shown++;

            if (shown == 5) {
                break;
            }

            ret += shown + ") " + row.getSender() + ": ";
            ret += row.getUrl();
            if (row.getUrlTitle() != null) {
                ret += " \"t: " + row.getUrlTitle() + "\"";
            }
            ret += " [" + StringStuff.formatNiceDate(row.getCreated(), false) + "]";
            response.addResponse(ret);
        }

    }

}
