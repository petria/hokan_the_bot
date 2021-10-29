package org.freakz.hokan_ng_springboot.bot.engine.command.handlers;

import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.UnflaggedOption;
import org.freakz.hokan_ng_springboot.bot.common.events.EngineResponse;
import org.freakz.hokan_ng_springboot.bot.common.events.InternalRequest;
import org.freakz.hokan_ng_springboot.bot.common.events.ServiceRequestType;
import org.freakz.hokan_ng_springboot.bot.common.events.ServiceResponse;
import org.freakz.hokan_ng_springboot.bot.common.exception.HokanException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import static org.freakz.hokan_ng_springboot.bot.common.util.StaticStrings.ARG_HORO;

/**
 * Created by Petri Airio (petri.j.airio@gmail.com) on 26.4.2015.
 */
@Component
@Scope("prototype")
public class HoroCmd extends Cmd {

    public HoroCmd() {

        UnflaggedOption opt = new UnflaggedOption(ARG_HORO)
                .setRequired(true)
                .setGreedy(false);
        registerParameter(opt);

    }


    @Override
    public void handleRequest(InternalRequest request, EngineResponse response, JSAPResult results) throws HokanException {
        String horo = results.getString(ARG_HORO);
        ServiceResponse serviceResponse = doServicesRequest(ServiceRequestType.HORO_REQUEST, request.getIrcEvent(), horo);
        String hh = serviceResponse.getHoroResponse();
        if (hh != null) {
            response.setResponseMessage(hh);
        } else {
            response.setResponseMessage("Saat dildoo perään ja et pääse pylsimään!");
        }

    }

}
