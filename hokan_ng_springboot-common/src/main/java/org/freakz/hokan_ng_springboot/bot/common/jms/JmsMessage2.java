package org.freakz.hokan_ng_springboot.bot.common.jms;

import org.freakz.hokan_ng_springboot.bot.common.enums.HokanModule;
import org.freakz.hokan_ng_springboot.bot.common.events.ServiceRequest;
import org.freakz.hokan_ng_springboot.bot.common.events.ServiceResponse2;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by petria on 4.2.2015.
 */
public class JmsMessage2<T> implements Serializable {

    private Map<String, Object> payload = new HashMap<>();

    private HokanModule sender;

    public Map<String, Object> getPayload() {
        return payload;
    }

    public void setPayload(Map<String, Object> payload) {
        this.payload = payload;
    }

    public void clearPayload() {
        this.payload = new HashMap<>();
    }

    public void addPayLoadObject(String key, Object data) {
        this.payload.put(key, data);
    }

    public Object getPayLoadObject(String key) {
        return this.payload.get(key);
    }


    public ServiceRequest getServiceRequest() {
        ServiceRequest request = (ServiceRequest) getPayLoadObject("SERVICE_REQUEST");
        return request;
    }

    public ServiceResponse2<T> getServiceResponse() {
        return (ServiceResponse2<T>) getPayLoadObject("SERVICE_RESPONSE2");
    }


    public HokanModule getSender() {
        return sender;
    }

    public void setSender(HokanModule sender) {
        this.sender = sender;
    }

    public String toString() {
        String values = "";
        for (String key : payload.keySet()) {
            values += " ";
            values += key + "=" + payload.get(key);
        }
        return this.getClass() + " :: " + values;

    }
}
