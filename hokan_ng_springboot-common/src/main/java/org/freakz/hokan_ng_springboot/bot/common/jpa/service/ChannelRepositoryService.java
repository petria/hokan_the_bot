package org.freakz.hokan_ng_springboot.bot.common.jpa.service;

import org.freakz.hokan_ng_springboot.bot.common.jpa.entity.Channel;
import org.freakz.hokan_ng_springboot.bot.common.jpa.entity.ChannelState;
import org.freakz.hokan_ng_springboot.bot.common.jpa.entity.Network;
import org.freakz.hokan_ng_springboot.bot.common.jpa.repository.ChannelRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Created by JohnDoe on 22.2.2015.
 */
@Service
public class ChannelRepositoryService implements ChannelService {

    private static final Logger log = LoggerFactory.getLogger(ChannelRepositoryService.class);

    @Autowired
    private ChannelRepository repository;

    @Override
    @Transactional(readOnly = true)
    public Channel findOne(long id) {
        Optional<Channel> byId = repository.findById(id);
        if (byId.isPresent()) {
            return byId.get();
        }
        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Channel> findChannels(Network network, ChannelState channelState) {
        return repository.findByNetworkAndChannelState(network, channelState);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Channel> findAll() {
        return repository.findAll();
    }

    @Override
    @Transactional(readOnly = false)
    public void resetChannelStates() {
        for (Channel channel : repository.findAll()) {
            channel.setChannelState(ChannelState.NONE);
            repository.save(channel);
        }
        log.debug("Channel states reset to: {}", ChannelState.NONE);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Channel> findByChannelNameLike(String like) {
        return repository.findByChannelNameLike(like);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Channel> findByNetwork(Network network) {
        return repository.findByNetwork(network);
    }

    @Override
    @Transactional(readOnly = true)
    public Channel findByNetworkAndChannelName(Network network, String channelName) {
        return repository.findByNetworkAndChannelName(network, channelName);
    }

    @Override
    @Transactional
    public Channel save(Channel object) {
        return repository.save(object);
    }

    @Override
    @Transactional
    public void delete(Channel object) {
        repository.delete(object);
    }

    @Override
    @Transactional
    public Channel create(Channel newRow) {
        return repository.save(newRow);
    }

    @Override
    @Transactional
    public Channel createChannel(Network network, String channelName) {
        Channel channel = new Channel(network, channelName);
        return repository.save(channel);
    }

    @Override
    @Transactional
    public void deleteAllByNetwork(Network object) {
        repository.deleteByNetwork(object);
    }
}
