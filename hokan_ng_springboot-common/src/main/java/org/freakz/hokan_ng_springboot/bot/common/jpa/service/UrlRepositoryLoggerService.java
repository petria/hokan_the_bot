package org.freakz.hokan_ng_springboot.bot.common.jpa.service;

import org.freakz.hokan_ng_springboot.bot.common.jpa.entity.Url;
import org.freakz.hokan_ng_springboot.bot.common.jpa.repository.UrlRepository;
import org.freakz.hokan_ng_springboot.bot.common.models.StartAndEndTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Optional;

/**
 * Created by Petri Airio (petri.j.airio@gmail.com) on 15.4.2015.
 */
@Service
public class UrlRepositoryLoggerService implements UrlLoggerService {

    private static final Logger log = LoggerFactory.getLogger(UrlRepositoryLoggerService.class);

    @Autowired
    private UrlRepository repository;

    @Override
    public List<Url> findByUrl(String url) {
        String likeUrl = "%" + url + "%";
        return repository.findByUrlLikeOrUrlTitleLikeOrderByCreatedDesc(likeUrl, likeUrl);
    }

    @Override
    public List<Url> findByUrlAndNicks(String url, String... nicks) {
        List<String> nickList = Arrays.asList(nicks);
        String likeUrl = "%" + url + "%";
        return repository.findByUrlLikeOrUrlTitleLikeAndSenderInOrderByCreatedDesc(likeUrl, likeUrl, nickList);
    }

    @Override
    public List<Url> findAll() {
        return repository.findAll();
    }

    @Override
    public Url findOne(long id) {
        Optional<Url> byId = repository.findById(id);
        return byId.orElseGet(byId::get);
    }

    @Override
    public List findTopSender() {
        return repository.findTopSender();
    }

    @Override
    public List findTopSenderByChannel(String channel) {
        return repository.findTopSenderByChannel(channel);
    }

    @Override
    public List findTopSenderByChannelAndCreatedBetween(String channel, StartAndEndTime saet) {
        ZonedDateTime zdtStart = ZonedDateTime.of(saet.getStartTime(), ZoneId.systemDefault());
        GregorianCalendar calStart = GregorianCalendar.from(zdtStart);

        ZonedDateTime zdtEnd = ZonedDateTime.of(saet.getEndTime(), ZoneId.systemDefault());
        GregorianCalendar calEnd = GregorianCalendar.from(zdtEnd);

        return repository.findTopSenderByChannelAndCreatedBetween(channel, calStart.getTime(), calEnd.getTime());
    }

    @Override
    public List<Url> findByCreatedBetweenAndChannel(StartAndEndTime saet, String channel) {
        ZonedDateTime zdtStart = ZonedDateTime.of(saet.getStartTime(), ZoneId.systemDefault());
        GregorianCalendar calStart = GregorianCalendar.from(zdtStart);

        ZonedDateTime zdtEnd = ZonedDateTime.of(saet.getEndTime(), ZoneId.systemDefault());
        GregorianCalendar calEnd = GregorianCalendar.from(zdtEnd);

        return repository.findByCreatedBetweenAndChannel(calStart.getTime(), calEnd.getTime(), channel);
    }

}
