package org.freakz.hokan_ng_springboot.bot.engine.command.handlers;

import com.martiansoftware.jsap.JSAPResult;
import org.freakz.hokan_ng_springboot.bot.common.events.EngineResponse;
import org.freakz.hokan_ng_springboot.bot.common.events.InternalRequest;
import org.freakz.hokan_ng_springboot.bot.common.exception.HokanException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * User: petria
 * Date: 11/28/13
 * Time: 9:32 AM
 *
 * @author Petri Airio <petri.j.airio@gmail.com>
 */
@Component
@Scope("prototype")
public class ExampleCmd extends Cmd {

    @Override
//    @PreAuthorize("hasRole('ADMIN')")
    public void handleRequest(InternalRequest request, EngineResponse response, JSAPResult results) throws HokanException {
        response.addResponse("example test1234567: %s", request.getIrcEvent().getMessage());
    }

}
