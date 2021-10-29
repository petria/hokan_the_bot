package org.freakz.hokan_ng_springboot.bot.common.jpa.service;

import org.freakz.hokan_ng_springboot.bot.common.jpa.entity.Network;
import org.freakz.hokan_ng_springboot.bot.common.jpa.repository.NetworkRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Created by Petri Airio on 19.2.2015.
 */
@Service
public class RepositoryNetworkService implements NetworkService {

    private static final Logger log = LoggerFactory.getLogger(RepositoryNetworkService.class);

    @Autowired
    private NetworkRepository networkRepository;

    @Override
    @Transactional
    public Network create(String networkName) {
        Network network = new Network(networkName);
        return networkRepository.save(network);
    }

    @Override
    @Transactional(readOnly = true)
    public Network getNetwork(String networkName) {
        return networkRepository.findByNetworkName(networkName);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Network> findAll() {
        return networkRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Network findOne(long id) {
        Optional<Network> byId = networkRepository.findById(id);
        if (byId.isPresent()) {
            return byId.get();
        }
        return null;
    }

    @Override
    @Transactional
    public void delete(Network network) {
        this.networkRepository.delete(network);
    }

    @Override
    @Transactional
    public Network save(Network network) {
        return this.networkRepository.save(network);
    }
}
