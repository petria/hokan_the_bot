package org.freakz.hokan_ng_springboot.bot.common.events;

import lombok.Data;

import java.io.Serializable;

@Data
public class SendSMSResponse implements Serializable {

    private String status;
    private String errorCode;
    private String errorReason;
    private String smsID;
    private String cost;
    private String parts;
    private String mccmnc;
    private String credits;

}
