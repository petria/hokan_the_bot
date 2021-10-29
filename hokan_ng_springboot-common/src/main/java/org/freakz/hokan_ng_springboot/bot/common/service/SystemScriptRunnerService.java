package org.freakz.hokan_ng_springboot.bot.common.service;

import org.freakz.hokan_ng_springboot.bot.common.models.SystemScriptResult;

/**
 * Created by Petri Airio (petri.j.airio@gmail.com) on 29.10.2015.
 * -
 */
public interface SystemScriptRunnerService {

    String[] runScript(SystemScript systemScript, String... args);

    SystemScriptResult runAndGetResult(SystemScript systemScript, String... args);

}
