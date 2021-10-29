package org.freakz.hokan_ng_springboot.bot.services.service.topics;

import org.freakz.hokan_ng_springboot.bot.common.events.IrcMessageEvent;
import org.freakz.hokan_ng_springboot.bot.common.models.ChannelSetTopic;

/**
 * Created by Petri Airio (petri.j.airio@gmail.com) 09/11/2016 / 11.18
 */
public interface TopicService {

    void channelTopicSet(IrcMessageEvent ircMessageEvent);

    ChannelSetTopic channelTopicGet(IrcMessageEvent ircMessageEvent);

}
