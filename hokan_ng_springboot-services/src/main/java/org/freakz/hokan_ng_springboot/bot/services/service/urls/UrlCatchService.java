package org.freakz.hokan_ng_springboot.bot.services.service.urls;

import org.freakz.hokan_ng_springboot.bot.common.events.ServiceRequest;

/**
 * Created by Petri Airio on 27.8.2015.
 */
public interface UrlCatchService {

    void catchUrls(ServiceRequest request);

}
