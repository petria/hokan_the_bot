package org.freakz.hokan_ng_springboot.bot.common.jpa.service;

import org.freakz.hokan_ng_springboot.bot.common.jpa.entity.Url;
import org.freakz.hokan_ng_springboot.bot.common.models.StartAndEndTime;

import java.util.List;

/**
 * Created by Petri Airio (petri.j.airio@gmail.com) on 15.4.2015.
 */
public interface UrlLoggerService {

    List<Url> findByUrl(String url);

    List<Url> findByUrlAndNicks(String url, String... nicks);

    List<Url> findAll();

    Url findOne(long id);

    List findTopSender();

    List findTopSenderByChannel(String channel);

    List findTopSenderByChannelAndCreatedBetween(String channel, StartAndEndTime saet);

    List<Url> findByCreatedBetweenAndChannel(StartAndEndTime saet, String channel);


}
