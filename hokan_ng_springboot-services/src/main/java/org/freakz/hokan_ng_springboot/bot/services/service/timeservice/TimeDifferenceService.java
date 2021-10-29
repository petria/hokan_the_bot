package org.freakz.hokan_ng_springboot.bot.services.service.timeservice;

import org.freakz.hokan_ng_springboot.bot.common.models.TimeDifferenceData;

import java.time.LocalDateTime;

public interface TimeDifferenceService {

    TimeDifferenceData getTimeDifference(LocalDateTime fromDateTime, LocalDateTime toDateTime);

}
