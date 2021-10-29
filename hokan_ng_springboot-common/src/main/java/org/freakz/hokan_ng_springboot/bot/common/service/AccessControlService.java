package org.freakz.hokan_ng_springboot.bot.common.service;

import org.freakz.hokan_ng_springboot.bot.common.jpa.entity.Channel;
import org.freakz.hokan_ng_springboot.bot.common.jpa.entity.User;
import org.freakz.hokan_ng_springboot.bot.common.jpa.entity.UserFlag;

import java.util.Set;

/**
 * Created by Petri Airio on 23.3.2015.
 */
public interface AccessControlService {

    boolean isAdminUser(User user);

    boolean isChannelOp(User user, Channel Channel);

    boolean isLoggedIn(User isLoggedIn);

    boolean authenticate(User user, String password);

    User loginUser(User user2);

    User logoffUser(User user2);

    boolean hasUserFlag(User user, UserFlag flag);

    User addUserFlags(User user, Set<UserFlag> flagSet);

    User removeUserFlags(User user, Set<UserFlag> flagSet);

}
