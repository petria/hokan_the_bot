package org.freakz.hokan_ng_springboot.bot.common.jpa.repository;

import org.freakz.hokan_ng_springboot.bot.common.jpa.entity.IrcServerConfig;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by JohnDoe on 22.2.2015.
 */
public interface IrcServerConfigRepository extends JpaRepository<IrcServerConfig, Long> {
}
