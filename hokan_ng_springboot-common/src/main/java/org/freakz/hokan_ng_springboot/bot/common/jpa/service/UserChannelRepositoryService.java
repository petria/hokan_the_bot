package org.freakz.hokan_ng_springboot.bot.common.jpa.service;

import org.freakz.hokan_ng_springboot.bot.common.jpa.entity.Channel;
import org.freakz.hokan_ng_springboot.bot.common.jpa.entity.User;
import org.freakz.hokan_ng_springboot.bot.common.jpa.entity.UserChannel;
import org.freakz.hokan_ng_springboot.bot.common.jpa.repository.UserChannelRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by Petri Airio on 7.4.2015.
 * -
 */
@Service
public class UserChannelRepositoryService implements UserChannelService {

    private static final Logger log = LoggerFactory.getLogger(UserChannelRepositoryService.class);

    @Autowired
    private UserChannelRepository repository;

    @Override
    @Transactional
    public UserChannel createUserChannel(User user, Channel channel) {
        UserChannel userChannel = new UserChannel(user, channel);
        return repository.save(userChannel);
    }

    @Override
    @Transactional(readOnly = true)
    public UserChannel getUserChannel(User user, Channel channel) {
        return repository.findFirstByUserAndChannel(user, channel);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserChannel> findByUser(User user) {
        return repository.findByUser(user);
    }

    @Override
    @Transactional
    public UserChannel save(UserChannel userChannel) {
        return repository.save(userChannel);
    }
}
