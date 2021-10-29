package org.freakz.hokan_ng_springboot.bot.services.service.sms;

import org.freakz.hokan_ng_springboot.bot.common.events.*;
import org.freakz.hokan_ng_springboot.bot.services.service.annotation.ServiceMessageHandler;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SendSMSRequestHandler {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(SendSMSRequestHandler.class);

    private final SMSSenderService smsSenderService;

    @Autowired
    public SendSMSRequestHandler(SMSSenderService smsSenderService) {
        this.smsSenderService = smsSenderService;
    }

    @ServiceMessageHandler(ServiceRequestType = ServiceRequestType.SMS_SEND_SERVICE_REQUEST)
    public void handleSMSSendRequest(ServiceRequest request, ServiceResponse response) {
        SendSMSRequest smsRequest = (SendSMSRequest) request.getParameters()[0];
        String answer = smsSenderService.sendSMS("_Hokan_", smsRequest.getTarget(), smsRequest.getMessage());
        log.debug("SMS answer: {}", answer);
        if (answer != null) {
            SendSMSResponse smsResponse = new SendSMSResponse();
            String[] split = answer.split(" ");
            if (answer.startsWith("OK")) {
                smsResponse.setStatus("OK");
                smsResponse.setSmsID(split[1]);
                smsResponse.setCost(split[2]);
                smsResponse.setParts(split[3]);
                smsResponse.setMccmnc(split[4]);
                smsResponse.setCredits(split[5]);
            } else {
                smsResponse.setStatus("NOK");
                smsResponse.setErrorCode(split[0]);
                switch (split[1]) {
                    case "1001":
                        smsResponse.setErrorReason("Not enough credits to send messages");
                        break;
                    case "1002":
                        smsResponse.setErrorReason("Identification failed. Wrong credentials");
                        break;
                    case "1003":
                        smsResponse.setErrorReason("Account not active");
                        break;
                    case "1004":
                        smsResponse.setErrorReason("This IP address is not added to this account. No access to API");
                        break;
                    case "1005":
                        smsResponse.setErrorReason("No handle provided");
                        break;
                    case "1006":
                        smsResponse.setErrorReason("No UserID provided");
                        break;
                    case "1007":
                        smsResponse.setErrorReason("No Username provided");
                        break;
                    case "2001":
                        smsResponse.setErrorReason("SMS message text is empty");
                        break;
                    case "2002":
                        smsResponse.setErrorReason("SMS numeric senderid can be max. 16 numbers");
                        break;
                    case "2003":
                        smsResponse.setErrorReason("SMS alphanumeric sender can be max. 11 characters");
                        break;
                    case "2004":
                        smsResponse.setErrorReason("SMS senderid is empty or invalid");
                        break;
                    case "2005":
                        smsResponse.setErrorReason("Destination number is too short");
                        break;
                    case "2006":
                        smsResponse.setErrorReason("Destination is not numeric");
                        break;
                    case "2007":
                        smsResponse.setErrorReason("Destination is empty");
                        break;
                    case "2008":
                        smsResponse.setErrorReason("SMS text is not OK (check encoding?)");
                        break;
                    case "2009":
                        smsResponse.setErrorReason("Parameter issue (check all mandatory parameters)");
                        break;
                    case "2010":
                        smsResponse.setErrorReason("Destination number is invalidly formatted");
                        break;
                    case "2011":
                        smsResponse.setErrorReason("Destination is invalid");
                        break;
                    case "2012":
                        smsResponse.setErrorReason("SMS message text is too long");
                        break;
                    case "2013":
                        smsResponse.setErrorReason("SMS message is invalid");
                        break;
                    case "2014":
                        smsResponse.setErrorReason("SMS CustomID is used before");
                        break;
                    case "2015":
                        smsResponse.setErrorReason("Charset problem");
                        break;
                    case "2016":
                        smsResponse.setErrorReason("Invalid UTF-8 encoding");
                        break;
                    case "2017":
                        smsResponse.setErrorReason("Invalid SMSid");
                        break;
                    case "3001":
                        smsResponse.setErrorReason("No route to destination");
                        break;
                    case "3002":
                        smsResponse.setErrorReason("No routes are setup");
                        break;
                    case "3003":
                        smsResponse.setErrorReason("Invalid destination. Check international mobile number formatting");
                        break;
                    case "4001":
                        smsResponse.setErrorReason("System error, related to customID");
                        break;
                    case "4002":
                        smsResponse.setErrorReason("System error, temporary issue. Try resubmitting in 2 to 3 minutes");
                        break;
                    case "4003":
                        smsResponse.setErrorReason("System error, temporary issue");
                        break;
                    case "4004":
                        smsResponse.setErrorReason("System error, temporary issue");
                        break;
                    case "4005":
                        smsResponse.setErrorReason("System error, permanent");
                        break;
                    case "4006":
                        smsResponse.setErrorReason("Gateway not reachable");
                        break;
                    case "4007":
                        smsResponse.setErrorReason("System error");
                        break;
                    case "5001":
                        smsResponse.setErrorReason("Send error");
                        break;
                    case "5002":
                        smsResponse.setErrorReason("Wrong SMS type");
                        break;
                    case "5003":
                        smsResponse.setErrorReason("Wrong operator");
                        break;
                    case "6001":
                        smsResponse.setErrorReason("Unknown error");
                        break;
                    case "7001":
                        smsResponse.setErrorReason("No HLR provider present");
                        break;
                    case "7002":
                        smsResponse.setErrorReason("Unexpected result from HLR provider");
                        break;
                    case "7003":
                        smsResponse.setErrorReason("Bad number format");
                        break;
                    case "7901":
                        smsResponse.setErrorReason("Unexpected error");
                        break;
                    case "7902":
                        smsResponse.setErrorReason("HLR provider error");
                        break;
                    case "7903":
                        smsResponse.setErrorReason("HLR provider error");
                        break;

                }
            }
            response.setResponseData(request.getType().getResponseDataKey(), smsResponse);
        }
    }

    @ServiceMessageHandler(ServiceRequestType = ServiceRequestType.SMS_CREDIT_SERVICE_REQUEST)
    public void handleSMSCreditRequest(ServiceRequest request, ServiceResponse response) {
        String answer = smsSenderService.getSMSCredits();
        log.debug("SMS credits: {}", answer);
        if (answer != null) {
            response.setResponseData(request.getType().getResponseDataKey(), answer);
        }
    }
}
