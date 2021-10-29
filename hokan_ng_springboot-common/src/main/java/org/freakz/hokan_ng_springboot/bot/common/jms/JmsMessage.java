package org.freakz.hokan_ng_springboot.bot.common.jms;

import org.freakz.hokan_ng_springboot.bot.common.enums.HokanModule;
import org.freakz.hokan_ng_springboot.bot.common.events.ServiceRequest;
import org.freakz.hokan_ng_springboot.bot.common.events.ServiceResponse;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by petria on 4.2.2015.
 */
public class JmsMessage implements Serializable {

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

    public String getCommand() {
        String command = (String) getPayLoadObject("COMMAND");
        if (command == null) {
            return "<null>";
        }
        return command;
    }

    public ServiceRequest getServiceRequest() {
        ServiceRequest request = (ServiceRequest) getPayLoadObject("SERVICE_REQUEST");
        return request;
    }

    public ServiceResponse getServiceResponse() {
        ServiceResponse response = (ServiceResponse) getPayLoadObject("SERVICE_RESPONSE");
        return response;
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
