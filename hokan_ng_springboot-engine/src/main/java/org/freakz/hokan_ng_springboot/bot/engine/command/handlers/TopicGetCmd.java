package org.freakz.hokan_ng_springboot.bot.engine.command.handlers;

import com.martiansoftware.jsap.JSAPResult;
import org.freakz.hokan_ng_springboot.bot.common.events.EngineResponse;
import org.freakz.hokan_ng_springboot.bot.common.events.InternalRequest;
import org.freakz.hokan_ng_springboot.bot.common.events.ServiceRequestType;
import org.freakz.hokan_ng_springboot.bot.common.events.ServiceResponse;
import org.freakz.hokan_ng_springboot.bot.common.exception.HokanException;
import org.freakz.hokan_ng_springboot.bot.common.models.ChannelSetTopic;
import org.freakz.hokan_ng_springboot.bot.common.util.StringStuff;
import org.freakz.hokan_ng_springboot.bot.engine.command.HelpGroup;
import org.freakz.hokan_ng_springboot.bot.engine.command.annotation.HelpGroups;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Created by Petri Airio on 21.9.2015.
 * -
 */
@Component
@Scope("prototype")

@HelpGroups(
        helpGroups = {HelpGroup.CHANNELS}
)
public class TopicGetCmd extends Cmd {

    public TopicGetCmd() {
        setHelp("Shows channel topic.");
    }

    @Override
    public void handleRequest(InternalRequest request, EngineResponse response, JSAPResult results) throws HokanException {
        ServiceResponse serviceResponse = doServicesRequest(ServiceRequestType.CHANNEL_TOPIC_GET_REQUEST, request.getIrcEvent(), "");
        ChannelSetTopic setTopic = serviceResponse.getChannelSetTopic();
        if (setTopic != null) {
            response.addResponse("Topic '%s' set by %s at %s.", setTopic.getTopic(), setTopic.getSender(), StringStuff.formatNiceDate(setTopic.getTimestamp(), true, false));
        } else {
            response.addResponse("n/a");
        }
    }

}
