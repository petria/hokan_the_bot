package org.freakz.hokan_ng_springboot.bot.common.jpa.repository;

import org.freakz.hokan_ng_springboot.bot.common.jpa.entity.Channel;
import org.freakz.hokan_ng_springboot.bot.common.jpa.entity.JoinedUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by Petri Airio on 1.4.2015.
 */
public interface JoinedUserRepository extends JpaRepository<JoinedUser, Long> {

    void deleteByChannel(Channel channel);

    List<JoinedUser> findByChannel(Channel channel);

}
