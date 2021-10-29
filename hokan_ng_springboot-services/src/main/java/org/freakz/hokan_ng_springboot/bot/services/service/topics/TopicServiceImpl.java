package org.freakz.hokan_ng_springboot.bot.services.service.topics;

import org.freakz.hokan_ng_springboot.bot.common.events.IrcMessageEvent;
import org.freakz.hokan_ng_springboot.bot.common.models.ChannelSetTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Petri Airio (petri.j.airio@gmail.com) 09/11/2016 / 11.30
 */
@Scope("singleton")
@Service
public class TopicServiceImpl implements TopicService {

    private static final Logger log = LoggerFactory.getLogger(TopicServiceImpl.class);

    private Map<String, ChannelSetTopic> topicMap = new ConcurrentHashMap<>();

    @Override
    public void channelTopicSet(IrcMessageEvent ircMessageEvent) {
        String key = String.format("%s#%s", ircMessageEvent.getNetwork(), ircMessageEvent.getChannel());
        ChannelSetTopic setTopic = topicMap.get(key);
        if (setTopic == null) {
            setTopic = new ChannelSetTopic();
        }
        setTopic.setNetwork(ircMessageEvent.getNetwork());
        setTopic.setChannel(ircMessageEvent.getChannel());

        String sender = ircMessageEvent.getSender();
        if (sender.contains("!")) {
            sender = sender.substring(0, sender.indexOf("!"));
        }
        setTopic.setSender(sender);
        setTopic.setTopic(ircMessageEvent.getMessage());
        Date d = new Date(ircMessageEvent.getTimestamp());
        setTopic.setTimestamp(d);

        topicMap.put(key, setTopic);
        log.debug("topic set: {}", setTopic);

    }

    @Override
    public ChannelSetTopic channelTopicGet(IrcMessageEvent ircMessageEvent) {
        String key = String.format("%s#%s", ircMessageEvent.getNetwork(), ircMessageEvent.getChannel());
        ChannelSetTopic setTopic = topicMap.get(key);
        return setTopic;
    }

}
