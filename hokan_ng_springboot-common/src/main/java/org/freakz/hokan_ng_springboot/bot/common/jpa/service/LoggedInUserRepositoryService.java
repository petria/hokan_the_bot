package org.freakz.hokan_ng_springboot.bot.common.jpa.service;

import org.freakz.hokan_ng_springboot.bot.common.jpa.entity.LoggedInState;
import org.freakz.hokan_ng_springboot.bot.common.jpa.entity.LoggedInUser;
import org.freakz.hokan_ng_springboot.bot.common.jpa.repository.LoggedInUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Petri Airio on 13.5.2015.
 */
@Service
public class LoggedInUserRepositoryService implements LoggedInUserService {

    @Autowired
    private LoggedInUserRepository repository;

    @Override
    public void invalidateAll() {
        List<LoggedInUser> all = repository.findAll();
        for (LoggedInUser user : all) {
            user.setLoggedInState(LoggedInState.INVALID);
        }
        repository.saveAll(all);
    }

}
