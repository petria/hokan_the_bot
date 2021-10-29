package org.freakz.hokan_ng_springboot.bot.services.service.servicerequesthandlers;

import org.freakz.hokan_ng_springboot.bot.common.events.ServiceRequest;
import org.freakz.hokan_ng_springboot.bot.common.events.ServiceRequestType;
import org.freakz.hokan_ng_springboot.bot.common.events.ServiceResponse;
import org.freakz.hokan_ng_springboot.bot.services.service.DayChangedService;
import org.freakz.hokan_ng_springboot.bot.services.service.annotation.ServiceMessageHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Petri Airio on 11.5.2016.
 * -
 */
@Component
public class SunriseServiceRequestHandler {

    private final DayChangedService dayChangedService;

    @Autowired
    public SunriseServiceRequestHandler(DayChangedService dayChangedService) {
        this.dayChangedService = dayChangedService;
    }

    @ServiceMessageHandler(ServiceRequestType = ServiceRequestType.SUNRISE_SERVICE_REQUEST)
    public void handleLunchPlacesServiceRequest(ServiceRequest request, ServiceResponse response) {
        String city = (String) request.getParameters()[0];
        List<String> cityList = new ArrayList<>();
        cityList.add(city);
        String sunrise = dayChangedService.getSunriseTexts(cityList);
        response.setResponseData(request.getType().getResponseDataKey(), sunrise);
    }

}
