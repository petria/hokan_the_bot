package org.freakz.hokan_ng_springboot.bot.common.util;

import org.freakz.hokan_ng_springboot.bot.common.enums.HostOS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Petri Airio on 10.3.2016.
 * -
 */
public class HostOsDetector {

    private static final Logger log = LoggerFactory.getLogger(HostOsDetector.class);

    public HostOS detectHostOs() {
        String OS = System.getProperty("os.name").toLowerCase();
        HostOS hostOS;
        if (OS.contains("win")) {
            hostOS = HostOS.WINDOWS;
        } else if (OS.contains("freebsd")) {
            hostOS = HostOS.BSD;
        } else if (OS.contains("mac")) {
            hostOS = HostOS.OSX;
        } else if (OS.contains("linux")) {
            hostOS = HostOS.LINUX;
        } else {
            hostOS = HostOS.UNKNOWN_OS;
        }
        log.debug("Detected OS: {}", hostOS.toString());
        return hostOS;
    }

}
