package org.freakz.hokan_ng_springboot.bot.common.service;

import org.freakz.hokan_ng_springboot.bot.common.enums.HostOS;
import org.freakz.hokan_ng_springboot.bot.common.models.SystemScriptResult;
import org.freakz.hokan_ng_springboot.bot.common.util.HostOsDetector;
import org.freakz.hokan_ng_springboot.bot.common.util.JarNixScriptExecutor;
import org.freakz.hokan_ng_springboot.bot.common.util.JarWindowsBatExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Created by Petri Airio (petri.j.airio@gmail.com) on 29.10.2015.
 * -
 */
@Service
public class SystemScriptRunnerServiceImpl implements SystemScriptRunnerService {

    private static final Logger log = LoggerFactory.getLogger(SystemScriptRunnerServiceImpl.class);

    private HostOsDetector osDetector = new HostOsDetector();

    @Override
    public String[] runScript(SystemScript systemScript, String... args) {
        HostOS hostOs = osDetector.detectHostOs();
        return runScript(systemScript, hostOs, args);
    }

    public String[] runScript(SystemScript systemScript, HostOS hostOs, String... args) {
        switch (hostOs) {
            case BSD:
            case LINUX:
            case OSX:
            case UNKNOWN_OS:
                return runNixSystemScript(systemScript, args);
            case WINDOWS:
                return runWindowsSystemScript(systemScript, args);
        }
        return new String[]{"N/A"};
    }


    @Override
    public SystemScriptResult runAndGetResult(SystemScript systemScript, String... args) {
        HostOS hostOs = osDetector.detectHostOs();
        String[] output = runScript(systemScript, hostOs, args);
        SystemScriptResult systemScriptResult = new SystemScriptResult();
        systemScriptResult.setOriginalOutput(output);
        if (output.length > 0) {
            systemScriptResult.setFormattedOutput(output[0]);
            switch (systemScript) {
                case HOST_INFO_SCRIPT:
                    if (hostOs == HostOS.WINDOWS) {
                        String name = output[2].substring(27).trim();
                        String version = output[3].substring(27).trim();
                        systemScriptResult.setFormattedOutput(name + " - " + version);
                    }
                    break;
            }
        }
        return systemScriptResult;
    }

    private String[] runWindowsSystemScript(SystemScript systemScript, String[] args) {
        JarWindowsBatExecutor executor = new JarWindowsBatExecutor(systemScript.getWindowsScript(), "windows-1252");
        return executor.executeJarWindowsBat(args);
    }

    private String[] runNixSystemScript(SystemScript systemScript, String[] args) {
        JarNixScriptExecutor executor = new JarNixScriptExecutor(systemScript.getNixScript(), "UTF-8");
        return executor.executeJarScript(args);
    }

}
