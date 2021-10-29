package org.freakz.hokan_ng_springboot.bot.common.jpa.service;

import org.freakz.hokan_ng_springboot.bot.common.jpa.entity.IrcServerConfig;
import org.freakz.hokan_ng_springboot.bot.common.jpa.repository.IrcServerConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created on 22.2.2015.
 */
@Service
public class RepositoryIrcServerConfigService implements IrcServerConfigService {

    @Autowired
    private IrcServerConfigRepository repository;

    @Override
    @Transactional(readOnly = true)
    public List<IrcServerConfig> findAll() {
        return repository.findAll();
    }

    @Override
    @Transactional
    public IrcServerConfig save(IrcServerConfig configuredServer) {
        return repository.save(configuredServer);
    }

    @Override
    public void delete(IrcServerConfig object) {
        repository.delete(object);
    }

}
