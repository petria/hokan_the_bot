package org.freakz.hokan_ng_springboot.bot.common.service;

import org.freakz.hokan_ng_springboot.bot.common.util.Uptime;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

/**
 * Created by Petri Airio (petri.j.airio@gmail.com) on 9.4.2015.
 * -
 */
@Service
@Scope("singleton")
public class UptimeServiceImpl implements UptimeService {

    private static Uptime uptime = new Uptime(System.currentTimeMillis());

    @Override
    public Uptime getUptime() {
        return uptime;
    }

}
