package org.freakz.hokan_ng_springboot.bot.common.jpa.service;

import org.freakz.hokan_ng_springboot.bot.common.jpa.entity.Channel;
import org.freakz.hokan_ng_springboot.bot.common.jpa.entity.ChannelState;
import org.freakz.hokan_ng_springboot.bot.common.jpa.entity.Network;

import java.util.List;

/**
 * Created by EXTAirioP on 20.2.2015.
 */
public interface ChannelService {


    Channel findOne(long id);

    List<Channel> findChannels(Network network, ChannelState joined);

    List<Channel> findAll();

    List<Channel> findByChannelNameLike(String like);

    List<Channel> findByNetwork(Network network);

    Channel findByNetworkAndChannelName(Network network, String channelName);

    Channel save(Channel object);

    void delete(Channel object);

    Channel create(Channel newRow);

    Channel createChannel(Network network, String channelName);

    void deleteAllByNetwork(Network object);

    void resetChannelStates();

}
