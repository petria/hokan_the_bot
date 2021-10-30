package org.freakz.hokan_ng_springboot.bot.common.jpa.repository;

import org.freakz.hokan_ng_springboot.bot.common.jpa.entity.Channel;
import org.freakz.hokan_ng_springboot.bot.common.jpa.entity.User;
import org.freakz.hokan_ng_springboot.bot.common.jpa.entity.UserChannel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by Petri Airio on 7.4.2015.
 */
public interface UserChannelRepository extends JpaRepository<UserChannel, Long> {

    List<UserChannel> findByUser(User user);

    UserChannel findFirstByUserAndChannel(User user, Channel channel);

}
