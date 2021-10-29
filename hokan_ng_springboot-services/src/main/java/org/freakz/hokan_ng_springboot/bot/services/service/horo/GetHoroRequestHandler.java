package org.freakz.hokan_ng_springboot.bot.services.service.horo;

import org.freakz.hokan_ng_springboot.bot.common.events.ServiceRequest;
import org.freakz.hokan_ng_springboot.bot.common.events.ServiceRequestType;
import org.freakz.hokan_ng_springboot.bot.common.events.ServiceResponse;
import org.freakz.hokan_ng_springboot.bot.services.service.annotation.ServiceMessageHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GetHoroRequestHandler {
/*
    @ServiceMessageHandler(ServiceRequestType = ServiceRequestType.HORO_REQUEST)
    public void handleHoroRequest(ServiceRequest request, ServiceResponse response) {
        HoroUpdater horoUpdater = (HoroUpdater) updaterManagerService.getUpdater("horoUpdater");
        UpdaterData updaterData = new UpdaterData();
        horoUpdater.getData(updaterData, request.getParameters());
        HoroHolder hh = (HoroHolder) updaterData.getData();
        response.setResponseData(request.getType().getResponseDataKey(), hh);
    }

 */

    @Autowired
    private HoroFetchService horoFetchService;

    @ServiceMessageHandler(ServiceRequestType = ServiceRequestType.HORO_REQUEST)
    public void handleHoroRequest(ServiceRequest request, ServiceResponse response) {
        String horo = (String) request.getParameters()[0];
        String responseText = horoFetchService.getHoro(horo);
        response.setResponseData(request.getType().getResponseDataKey(), responseText);
    }
}
