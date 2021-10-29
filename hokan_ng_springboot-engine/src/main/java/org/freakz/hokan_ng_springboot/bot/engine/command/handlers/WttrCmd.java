package org.freakz.hokan_ng_springboot.bot.engine.command.handlers;

import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.UnflaggedOption;
import org.freakz.hokan_ng_springboot.bot.common.events.EngineResponse;
import org.freakz.hokan_ng_springboot.bot.common.events.InternalRequest;
import org.freakz.hokan_ng_springboot.bot.common.exception.HokanException;
import org.freakz.hokan_ng_springboot.bot.common.util.StaticStrings;
import org.freakz.hokan_ng_springboot.bot.engine.command.HelpGroup;
import org.freakz.hokan_ng_springboot.bot.engine.command.annotation.HelpGroups;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.Authenticator;
import java.net.PasswordAuthentication;

import static org.freakz.hokan_ng_springboot.bot.common.util.StaticStrings.ARG_PLACE;

/**
 * Created by Petri Airio on 7.3.2016.
 * -
 */
@Component

@Scope("prototype")
@HelpGroups(
        helpGroups = {HelpGroup.DATA_FETCHERS}
)
public class WttrCmd extends Cmd {

    public WttrCmd() {
        UnflaggedOption opt = new UnflaggedOption(ARG_PLACE)
                .setDefault("Jyväskylä")
                .setRequired(true)
                .setGreedy(false);
        registerParameter(opt);
    }

    @Override
    public void handleRequest(InternalRequest request, EngineResponse response, JSAPResult results) throws HokanException {

        String url = "http://wttr.in/" + results.getString(ARG_PLACE);
        Document doc;
        try {
            Authenticator.setDefault(new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication("foo", "bar".toCharArray());
                }
            });
            doc = Jsoup.connect(url).timeout(0).userAgent(StaticStrings.HTTP_USER_AGENT).get();
        } catch (IOException e) {
            response.addResponse("Could not fetch data from %s", url);
            return;
        }
        Elements elements = doc.getElementsByTag("pre");
        if (elements.size() == 0) {
            response.addResponse("Nothing found with: %s", results.getString(ARG_PLACE));
            return;
        }
        String text = elements.text();
        String split[] = text.split("\n");
        for (int i = 0; i < 7; i++) {
            response.addResponse("%s\n", split[i]);
        }

    }
}
