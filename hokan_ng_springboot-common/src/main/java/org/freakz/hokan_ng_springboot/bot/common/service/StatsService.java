package org.freakz.hokan_ng_springboot.bot.common.service;

import org.freakz.hokan_ng_springboot.bot.common.models.StatsMapper;

import java.time.LocalDateTime;

/**
 * Created by Petri Airio on 21.8.2015.
 */
public interface StatsService {

    StatsMapper getDailyStatsForChannel(LocalDateTime day, String channel);

    StatsMapper getStatsForChannel(String channel);

}
