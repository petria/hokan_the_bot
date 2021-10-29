package org.freakz.hokan_ng_springboot.bot.engine.command.handlers;

import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.UnflaggedOption;
import org.freakz.hokan_ng_springboot.bot.common.events.EngineResponse;
import org.freakz.hokan_ng_springboot.bot.common.events.InternalRequest;
import org.freakz.hokan_ng_springboot.bot.common.events.ServiceRequestType;
import org.freakz.hokan_ng_springboot.bot.common.events.ServiceResponse;
import org.freakz.hokan_ng_springboot.bot.common.exception.HokanException;
import org.freakz.hokan_ng_springboot.bot.common.models.ChannelSetTopic;
import org.freakz.hokan_ng_springboot.bot.engine.command.HelpGroup;
import org.freakz.hokan_ng_springboot.bot.engine.command.annotation.HelpGroups;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import static org.freakz.hokan_ng_springboot.bot.common.util.StaticStrings.ARG_TOPIC;

/**
 * Created by JohnDoe on 9.11.2016.
 * -
 */
@Component
@Scope("prototype")

@HelpGroups(
        helpGroups = {HelpGroup.CHANNELS}
)
public class TopicSetCmd extends Cmd {

    public TopicSetCmd() {
        setHelp("Sets channel topic.");
        UnflaggedOption flg = new UnflaggedOption(ARG_TOPIC)
                .setRequired(false)
                .setGreedy(false);
        registerParameter(flg);

    }

    @Override
    public void handleRequest(InternalRequest request, EngineResponse response, JSAPResult results) throws HokanException {
        String topic = results.getString(ARG_TOPIC);
        String newTopic;
        if (topic.startsWith("+")) {
            ServiceResponse serviceResponse = doServicesRequest(ServiceRequestType.CHANNEL_TOPIC_GET_REQUEST, request.getIrcEvent(), "");
            ChannelSetTopic setTopic = serviceResponse.getChannelSetTopic();
            String oldTopic = "";
            if (setTopic != null) {
                oldTopic = setTopic.getTopic();
            }
            newTopic = oldTopic + " | " + topic.substring(1);
        } else {
            newTopic = topic;
        }
        response.addEngineMethodCall("setTopic", request.getChannel().getChannelName(), newTopic);

    }
}
