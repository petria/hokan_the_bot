package org.freakz.hokan_ng_springboot.bot.common.jpa.repository;

import org.freakz.hokan_ng_springboot.bot.common.jpa.entity.DataValues;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by Petri Airio on 31.8.2015.
 */
public interface DataValuesRepository extends JpaRepository<DataValues, Long> {

    DataValues findByNickAndChannelAndNetworkAndKeyName(String nick, String channel, String network, String keyName);

    List<DataValues> findAllByChannelAndNetworkAndKeyName(String channel, String network, String keyName);

    List<DataValues> findAllByChannelAndNetworkAndKeyNameIsLike(String channel, String network, String keyName);

    List<DataValues> findAllByNickAndChannelAndNetworkAndKeyNameIsLike(String nick, String channel, String network, String keyName);

    List<DataValues> findAllByChannelAndNetwork(String channel, String network);


}
