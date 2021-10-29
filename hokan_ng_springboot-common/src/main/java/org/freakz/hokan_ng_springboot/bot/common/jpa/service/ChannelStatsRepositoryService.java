package org.freakz.hokan_ng_springboot.bot.common.jpa.service;

import org.freakz.hokan_ng_springboot.bot.common.jpa.entity.Channel;
import org.freakz.hokan_ng_springboot.bot.common.jpa.entity.ChannelStats;
import org.freakz.hokan_ng_springboot.bot.common.jpa.repository.ChannelStatsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by Petri Airio on 8.4.2015.
 * -
 */
@Service
public class ChannelStatsRepositoryService implements ChannelStatsService {

    private static final Logger log = LoggerFactory.getLogger(ChannelStatsRepositoryService.class);

    private final ChannelStatsRepository repository;

    @Autowired
    public ChannelStatsRepositoryService(ChannelStatsRepository repository) {
        this.repository = repository;
    }

    @Override
    public ChannelStats findFirstByChannel(Channel channel) {
        return repository.findFirstByChannel(channel);
    }

    @Override
    public ChannelStats save(ChannelStats channelStats) {
        try {
            ChannelStats saved = repository.save(channelStats);
            return saved;
        } catch (Exception e) {
            log.error("Save failed...", e);
            log.error("... Failed entity: {}", channelStats.toString());
        }
        return channelStats;
    }

}
