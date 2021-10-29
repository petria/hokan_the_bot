package org.freakz.hokan_ng_springboot.bot.services.service.servicerequesthandlers;

import org.freakz.hokan_ng_springboot.bot.common.events.ServiceRequest;
import org.freakz.hokan_ng_springboot.bot.common.events.ServiceRequestType;
import org.freakz.hokan_ng_springboot.bot.common.events.ServiceResponse;
import org.freakz.hokan_ng_springboot.bot.common.models.SystemScriptResult;
import org.freakz.hokan_ng_springboot.bot.common.service.SystemScript;
import org.freakz.hokan_ng_springboot.bot.common.service.SystemScriptRunnerService;
import org.freakz.hokan_ng_springboot.bot.common.util.FileUtil;
import org.freakz.hokan_ng_springboot.bot.services.service.annotation.ServiceMessageHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Created by Petri Airio on 29.3.2016. -
 */
@Component
public class CharsetServiceRequestHandler {

    private final FileUtil fileUtil;

    private final SystemScriptRunnerService systemScriptRunnerService;

    @Autowired
    public CharsetServiceRequestHandler(FileUtil fileUtil, SystemScriptRunnerService systemScriptRunnerService) {
        this.fileUtil = fileUtil;
        this.systemScriptRunnerService = systemScriptRunnerService;
    }

    @ServiceMessageHandler(ServiceRequestType = ServiceRequestType.CHARSET_REQUEST)
    public void handleCharsetServiceRequest(ServiceRequest request, ServiceResponse response) {
        byte[] messageBytes = request.getIrcMessageEvent().getMessage().getBytes();
        try {
            String tmpFile = fileUtil.copyBytesToTmpFile(request.getIrcMessageEvent().getOriginal());
            // tmpFile = "D:\\TEMP\\bytes";
            SystemScriptResult result = systemScriptRunnerService.runAndGetResult(SystemScript.FILE_SCRIPT, tmpFile);
            response.setResponseData(request.getType().getResponseDataKey(), result.getOriginalOutput());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
