package org.freakz.hokan_ng_springboot.bot.common.jpa.service;

import org.freakz.hokan_ng_springboot.bot.common.jpa.entity.Channel;
import org.freakz.hokan_ng_springboot.bot.common.jpa.entity.JoinedUser;
import org.freakz.hokan_ng_springboot.bot.common.jpa.entity.Network;
import org.freakz.hokan_ng_springboot.bot.common.jpa.entity.User;
import org.freakz.hokan_ng_springboot.bot.common.jpa.repository.JoinedUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Petri Airio on 1.4.2015.
 */
@Service

public class JoinedUserRepositoryService implements JoinedUserService {

    private static final Logger log = LoggerFactory.getLogger(JoinedUserRepositoryService.class);

    @Autowired
    private JoinedUserRepository repository;

    @Override
    @Transactional(readOnly = false)
    public void clearJoinedUsers(Channel channel) {
        List<JoinedUser> joinedUsers = repository.findByChannel(channel);
        log.debug("Deleting {} joined users from channel {}", joinedUsers.size(), channel.getChannelName());
        repository.deleteAll(joinedUsers);
    }

    @Override
    @Transactional(readOnly = false)
    public JoinedUser createJoinedUser(Channel channel, User user, String userModes) {
        JoinedUser joinedUser = new JoinedUser(channel, user, userModes);
        return repository.save(joinedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public List<JoinedUser> findJoinedUsers(Channel channel) {
        return repository.findByChannel(channel);
    }

    @Override
    @Transactional(readOnly = true)
    public List<JoinedUser> findJoinedUsersByNetwork(Network network) {
        List<JoinedUser> all = repository.findAll();
        List<JoinedUser> inNetwork = new ArrayList<>();
        for (JoinedUser joinedUser : all) {
            if (joinedUser.getChannel().getNetwork().getId() == network.getId()) {
                inNetwork.add(joinedUser);
            }
        }
        return inNetwork;
    }
}
