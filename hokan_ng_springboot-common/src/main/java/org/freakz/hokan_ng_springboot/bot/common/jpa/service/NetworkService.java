package org.freakz.hokan_ng_springboot.bot.common.jpa.service;

import org.freakz.hokan_ng_springboot.bot.common.jpa.entity.Network;

import java.util.List;

/**
 * Created by Petri Airio on 10.2.2015.
 */
public interface NetworkService {

    Network create(String networkName);

    Network getNetwork(String networkName);

    List<Network> findAll();

    Network findOne(long id);

    void delete(Network network);

    Network save(Network network);

}
