package org.freakz.hokan_ng_springboot.bot.common.service;

/**
 * Created by Petri Airio (petri.j.airio@gmail.com) on 29.10.2015.
 * -
 */
public enum SystemScript {

    CAL_SCRIPT("/cal.sh", "/cal.bat"),
    FILE_SCRIPT("/file.sh", "/file.bat"),
    HOST_INFO_SCRIPT("/hostinfo.sh", "/hostinfo.bat"),
    UPTIME_SCRIPT("/uptime.sh", "/uptime.bat"),
    VPENIS_SCRIPT("/vpenis.sh", "/vpenis.bat");

    private final String nixScript;
    private final String windowsScript;

    SystemScript(String nixScript, String windowsScript) {
        this.nixScript = nixScript;
        this.windowsScript = windowsScript;
    }

    public String getNixScript() {
        return nixScript;
    }

    public String getWindowsScript() {
        return windowsScript;
    }

}
