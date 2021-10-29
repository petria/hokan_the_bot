package org.freakz.hokan_ng_springboot.bot.services.service.servicerequesthandlers;

import org.freakz.hokan_ng_springboot.bot.common.enums.LunchPlace;
import org.freakz.hokan_ng_springboot.bot.common.events.ServiceRequest;
import org.freakz.hokan_ng_springboot.bot.common.events.ServiceRequestType;
import org.freakz.hokan_ng_springboot.bot.common.events.ServiceResponse;
import org.freakz.hokan_ng_springboot.bot.common.models.LunchData;
import org.freakz.hokan_ng_springboot.bot.common.models.LunchPlaceData;
import org.freakz.hokan_ng_springboot.bot.services.service.annotation.ServiceMessageHandler;
import org.freakz.hokan_ng_springboot.bot.services.service.lunch.LunchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * Created by Petri Airio on 28.1.2016.
 * -
 */
@Component
public class LunchServiceRequestHandler {

    private static final Logger log = LoggerFactory.getLogger(LunchServiceRequestHandler.class);

    private final LunchService lunchService;

    @Autowired
    public LunchServiceRequestHandler(LunchService lunchService) {
        this.lunchService = lunchService;
    }

    @ServiceMessageHandler(ServiceRequestType = ServiceRequestType.LUNCH_PLACES_REQUEST)
    public void handleLunchPlacesServiceRequest(ServiceRequest request, ServiceResponse response) {
        log.debug("Handling: {}", request);
        List<LunchPlace> placeList = lunchService.getLunchPlaces();
        response.setResponseData(request.getType().getResponseDataKey(), placeList);
    }

    @ServiceMessageHandler(ServiceRequestType = ServiceRequestType.LUNCH_REQUEST)
    public void handleLunchRequest(ServiceRequest request, ServiceResponse response) {
        log.debug("Handling: {}", request);
        Set<LunchPlace> places = (Set<LunchPlace>) request.getParameters()[0];
        LocalDateTime day = (LocalDateTime) request.getParameters()[1];
        LunchPlaceData lunchPlaceData = new LunchPlaceData();
        for (LunchPlace place : places) {
            LunchData lunchData = lunchService.getLunchForDay(place, day);
            lunchPlaceData.getLunchDataMap().put(place, lunchData);
        }
        response.setResponseData(request.getType().getResponseDataKey(), lunchPlaceData);
    }

}
