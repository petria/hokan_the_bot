package org.freakz.hokan_ng_springboot.bot.common.jpa.service;

import org.freakz.hokan_ng_springboot.bot.common.jpa.entity.Alias;
import org.freakz.hokan_ng_springboot.bot.common.jpa.repository.AliasRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by Petri Airio on 31.8.2015.
 */
@Service
public class AliasRepositoryService implements AliasService {

    @Autowired
    private AliasRepository repository;

    @Override
    @Transactional(readOnly = true)
    public List<Alias> findAll() {
        return repository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Alias findFirstByAlias(String alias) {
        return repository.findFirstByAlias(alias);
    }

    @Override
    @Transactional(readOnly = false)
    public Alias save(Alias a) {
        return repository.save(a);
    }

    @Override
    @Transactional(readOnly = false)
    public int delete(Alias a) {
        repository.delete(a);
        return 1;
    }
}
