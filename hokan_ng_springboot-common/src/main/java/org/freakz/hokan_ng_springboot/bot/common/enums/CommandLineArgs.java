package org.freakz.hokan_ng_springboot.bot.common.enums;

/**
 * Created by Petri Airio on 13.11.2015.
 * -
 */
public enum CommandLineArgs {

    CONFIG_INIT("--ConfigInit"),
    JMS_BROKER_URL("--JmsBrokerUrl");

    private String commandLineArg;

    CommandLineArgs(String commandLineArg) {
        this.commandLineArg = commandLineArg;
    }

    public String getCommandLineArg() {
        return this.commandLineArg;
    }


}
