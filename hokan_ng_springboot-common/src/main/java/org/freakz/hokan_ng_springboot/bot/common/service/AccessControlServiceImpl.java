package org.freakz.hokan_ng_springboot.bot.common.service;


import org.freakz.hokan_ng_springboot.bot.common.jpa.entity.Channel;
import org.freakz.hokan_ng_springboot.bot.common.jpa.entity.User;
import org.freakz.hokan_ng_springboot.bot.common.jpa.entity.UserFlag;
import org.freakz.hokan_ng_springboot.bot.common.jpa.service.UserService;
import org.freakz.hokan_ng_springboot.bot.common.util.StringStuff;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * Created by Petri Airio on 23.3.2015.
 */
@Service

public class AccessControlServiceImpl implements AccessControlService {

    private static final Logger log = LoggerFactory.getLogger(AccessControlServiceImpl.class);

    @Autowired
    private UserService userService;

    @Override
    public boolean isAdminUser(User isAdmin) {
        User user = userService.findById(isAdmin.getId());
        if (user == null) {
            log.debug("User not found: {}", isAdmin);
            return false;
        }
        return user.getUserFlagsSet().contains(UserFlag.ADMIN);
    }

    @Override
    public boolean isChannelOp(User isChannelOp, Channel Channel) {
/*        User user = userService.findById(isChannelOp.getId());
        if (user == null) {
            log.debug("User not found: {}", isChannelOp);
            return false;
        }
        String flags = user.getFlags();
        if (flags == null) {
            log.debug("User {} flags null!", user);
            return false;
        }
        return flags.contains("C");*/
        return true;
    }

    @Override
    public boolean authenticate(User user, String password) {
        String sha1Password = StringStuff.getSHA1Password(password);
        if (user.getPassword().equals(sha1Password)) {
            user.setLoggedIn(1);
            userService.save(user);
            return true;
        }
        return false;
    }

    @Override
    public boolean isLoggedIn(User isLoggedIn) {
        User user = userService.findById(isLoggedIn.getId());
        if (user == null) {
            log.debug("User not found: {}", isLoggedIn);
            return false;
        }
        return user.isLoggedIn() > 0;
    }

    @Override
    public User loginUser(User user2) {
        User user = userService.findById(user2.getId());
        if (user == null) {
            log.debug("User not found: {}", user2);
            return null;
        }
        user.setLoggedIn(1);
        log.info("User logged in: {}", user);
        return userService.save(user);
    }

    @Override
    public User logoffUser(User user2) {
        User user = userService.findById(user2.getId());
        if (user == null) {
            log.debug("User not found: {}", user2);
            return null;
        }
        user.setLoggedIn(0);
        log.info("User logged off: {}", user);
        return userService.save(user);
    }

    @Override
    public boolean hasUserFlag(User user2, UserFlag flag) {
        User user = userService.findById(user2.getId());
        if (user == null) {
            log.debug("User not found: {}", user2);
            return false;
        }
        return user.getUserFlagsSet().contains(flag);
    }

    @Override
    public User addUserFlags(User user2, Set<UserFlag> flagSet) {
        User user = userService.findById(user2.getId());
        if (user == null) {
            log.debug("User not found: {}", user2);
            return user;
        }
        Set<UserFlag> oldFlagSet = user.getUserFlagsSet();
        for (UserFlag flag : flagSet) {
            if (!oldFlagSet.contains(flag)) {
                oldFlagSet.add(flag);
                log.debug("Added flag: {}", flag);
            }
        }
        user.setFlags(UserFlag.getStringFromFlagSet(oldFlagSet));
        return userService.save(user);
    }

    @Override
    public User removeUserFlags(User user2, Set<UserFlag> flagSet) {
        User user = userService.findById(user2.getId());
        if (user == null) {
            log.debug("User not found: {}", user2);
            return user;
        }
        Set<UserFlag> oldFlagSet = user.getUserFlagsSet();
        for (UserFlag flag : flagSet) {
            if (oldFlagSet.contains(flag)) {
                oldFlagSet.remove(flag);
                log.debug("Removed flag: {}", flag);
            }
        }
        user.setFlags(UserFlag.getStringFromFlagSet(oldFlagSet));
        return userService.save(user);

    }
}
