package org.freakz.hokan_ng_springboot.bot.common.jpa.service;

import org.freakz.hokan_ng_springboot.bot.common.jpa.entity.Alias;

import java.util.List;

/**
 * Created by Petri Airio on 31.8.2015.
 */
public interface AliasService {

    List<Alias> findAll();

    Alias findFirstByAlias(String alias);

    Alias save(Alias a);

    int delete(Alias a);

}
