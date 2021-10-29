package org.freakz.hokan_ng_springboot.bot.engine.command.handlers;

import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.UnflaggedOption;
import org.freakz.hokan_ng_springboot.bot.common.events.EngineResponse;
import org.freakz.hokan_ng_springboot.bot.common.events.InternalRequest;
import org.freakz.hokan_ng_springboot.bot.common.exception.HokanException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import static org.freakz.hokan_ng_springboot.bot.common.util.StaticStrings.ARG_TEXT;

/**
 * Created by Petri Airio on 15.1.2016.
 * -
 */
@Component
@Scope("prototype")

public class TestCmd extends Cmd {
    public TestCmd() {
        super();
        UnflaggedOption flg = new UnflaggedOption(ARG_TEXT)
                .setRequired(true)
                .setGreedy(false);
        registerParameter(flg);

    }

    @Override
    public void handleRequest(InternalRequest request, EngineResponse response, JSAPResult results) throws HokanException {
        String text = results.getString(ARG_TEXT);
        response.addResponse("Sedjdjddjdjdjdnt: %s", text);
    }

}
