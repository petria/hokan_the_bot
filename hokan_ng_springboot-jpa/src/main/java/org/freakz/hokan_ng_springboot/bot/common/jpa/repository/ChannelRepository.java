package org.freakz.hokan_ng_springboot.bot.common.jpa.repository;

import org.freakz.hokan_ng_springboot.bot.common.jpa.entity.Channel;
import org.freakz.hokan_ng_springboot.bot.common.jpa.entity.ChannelState;
import org.freakz.hokan_ng_springboot.bot.common.jpa.entity.Network;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created on 22.2.2015.
 */
public interface ChannelRepository extends JpaRepository<Channel, Long> {

    List<Channel> findByNetworkAndChannelState(Network network, ChannelState channelState);

    List<Channel> findByNetwork(Network network);

    void deleteByNetwork(Network object);

    Channel findByNetworkAndChannelName(Network network, String channelName);

    List<Channel> findByChannelNameLike(String like);

}
