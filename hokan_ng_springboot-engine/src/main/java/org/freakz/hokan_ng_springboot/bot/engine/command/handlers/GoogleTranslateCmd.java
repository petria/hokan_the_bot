package org.freakz.hokan_ng_springboot.bot.engine.command.handlers;

import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.UnflaggedOption;
import org.freakz.hokan_ng_springboot.bot.common.events.EngineResponse;
import org.freakz.hokan_ng_springboot.bot.common.events.InternalRequest;
import org.freakz.hokan_ng_springboot.bot.common.events.ServiceRequestType;
import org.freakz.hokan_ng_springboot.bot.common.events.ServiceResponse;
import org.freakz.hokan_ng_springboot.bot.common.exception.HokanException;

import static org.freakz.hokan_ng_springboot.bot.common.util.StaticStrings.ARG_TEXT;

/**
 * Created by Petri Airio (petri.j.airio@gmail.com) on 29.4.2015.
 */
//@Component
//@Scope("prototype")
// 
public class GoogleTranslateCmd extends Cmd {


    public GoogleTranslateCmd() {
        super();
        setHelp("Translate using Google API");

        UnflaggedOption flg = new UnflaggedOption(ARG_TEXT)
                .setRequired(true)
                .setGreedy(false);
        registerParameter(flg);
    }

/*  @Override
  public String getMatchPattern() {
    return "!googletrans.*|!gtrans.*";
  }*/

    @Override
    public void handleRequest(InternalRequest request, EngineResponse response, JSAPResult results) throws HokanException {

        String text = results.getString(ARG_TEXT);
        ServiceResponse serviceResponse = doServicesRequest(ServiceRequestType.TRANSLATE_REQUEST, request.getIrcEvent(), text);
        response.addResponse("%s", serviceResponse.getResponseData("TRANSLATE_RESPONSE"));
    }
}
