package org.freakz.hokan_ng_springboot.bot.common.events;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Petri Airio (petri.j.airio@gmail.com) on 24.4.2015.
 * -
 */
@SuppressWarnings("unchecked")
public class ServiceResponse2<T> implements Serializable {

    private final ServiceRequestType type;
    private final Map<String, Object> responseData = new HashMap<>();

    public ServiceResponse2(ServiceRequestType type) {
        this.type = type;
    }

    public void setResponseData(String key, Object data) {
        responseData.put(key, data);
    }

    public T getResponse() {
        return (T) responseData.get(type.getResponseDataKey());
    }
}
