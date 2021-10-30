package org.freakz.hokan_ng_springboot.bot.common.jpa.repository;

import org.freakz.hokan_ng_springboot.bot.common.jpa.entity.Network;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by Petri Airio on 19.2.2015.
 */
public interface NetworkRepository extends JpaRepository<Network, Long> {

    Network findByNetworkName(String networkName);

}
