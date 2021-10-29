package org.freakz.hokan_ng_springboot.bot.engine.command.handlers;

import com.martiansoftware.jsap.JSAPResult;
import org.freakz.hokan_ng_springboot.bot.common.events.EngineResponse;
import org.freakz.hokan_ng_springboot.bot.common.events.InternalRequest;
import org.freakz.hokan_ng_springboot.bot.common.exception.HokanException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * User: petria
 * Date: 16th Feb 2021
 *
 * @author Petri Airio <petri.j.airio@gmail.com>
 */
@Component
@Scope("prototype")
public class EchoCmd extends Cmd {

    public EchoCmd() {
        setHelp("Echo text back.");
    }

    @Override
    public void handleRequest(InternalRequest request, EngineResponse response, JSAPResult results) throws HokanException {
        String msg = request.getIrcEvent().getMessage();
        int idx = msg.indexOf(" ");
        if (idx != -1) {
            response.addResponse("%s", msg.substring(idx + 1));
        } else {
            response.addResponse("%s", "Kiekuuko kaiku, kuuntelen!");
        }
    }

}
