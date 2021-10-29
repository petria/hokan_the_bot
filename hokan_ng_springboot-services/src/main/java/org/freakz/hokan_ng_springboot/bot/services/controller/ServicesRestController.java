package org.freakz.hokan_ng_springboot.bot.services.controller;

import org.freakz.hokan_ng_springboot.bot.common.enums.HokanModule;
import org.freakz.hokan_ng_springboot.bot.common.events.NotifyRequest;
import org.freakz.hokan_ng_springboot.bot.common.jms.api.JmsSender;
import org.freakz.hokan_ng_springboot.bot.common.jpa.entity.Channel;
import org.freakz.hokan_ng_springboot.bot.common.jpa.entity.PropertyName;
import org.freakz.hokan_ng_springboot.bot.common.jpa.service.ChannelPropertyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
//@Transactional
public class ServicesRestController {

    private static final Logger log = LoggerFactory.getLogger(ServicesRestController.class);

    private final JmsSender jmsSender;

    private final ChannelPropertyService channelPropertyService;

    @Autowired
    public ServicesRestController(JmsSender jmsSender, ChannelPropertyService channelPropertyService) {
        this.jmsSender = jmsSender;
        this.channelPropertyService = channelPropertyService;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/torrent/added")
    public void torrentAdded(@RequestParam String name) {
        processReply("Torrent added: " + name);
        log.debug("Torrent added: {}", name);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/torrent/completed")
    public void torrentCompleted(@RequestParam String name) {
        processReply("Torrent completed: " + name);
        log.debug("Torrent completed: {}", name);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/torrent/removed")
    public void torrentRemoved(@RequestParam String name) {
        processReply("Torrent removed: " + name);
        log.debug("Torrent removed: {}", name);
    }

    private void processReply(String reply) {

        List<Channel> channelList = channelPropertyService.getChannelsWithProperty(PropertyName.PROP_CHANNEL_DO_TORRENTS, ".*");
        log.debug("Channels to send torrent message: {}", channelList.size());
        for (Channel channel : channelList) {
            NotifyRequest notifyRequest = new NotifyRequest();
            notifyRequest.setNotifyMessage(reply);
            notifyRequest.setTargetChannelId(channel.getId());
            jmsSender.send(HokanModule.HokanServices, HokanModule.HokanIo.getQueueName(), "TORRENT_NOTIFY_REQUEST", notifyRequest, false);
            log.debug("TORRENT_NOTIFY_REQUEST: " + notifyRequest.getTargetChannelId());
        }

    }

}
