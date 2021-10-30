package org.freakz.hokan_ng_springboot.bot.common.jpa.repository;

import org.freakz.hokan_ng_springboot.bot.common.jpa.entity.Alias;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by Petri Airio on 31.8.2015.
 */
public interface AliasRepository extends JpaRepository<Alias, Long> {

    Alias findFirstByAlias(String alias);

}
