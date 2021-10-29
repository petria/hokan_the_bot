package org.freakz.hokan_ng_springboot.bot.common.events;

import org.freakz.hokan_ng_springboot.bot.common.util.CommandArgs;

import java.io.Serializable;

/**
 * Created by Petri Airio on 22.4.2015.
 */
public class ServiceRequest implements Serializable {


    private final ServiceRequestType type;
    private final IrcMessageEvent ircMessageEvent;
    private final CommandArgs commandArgs;
    private final Object[] parameters;

    public ServiceRequest(ServiceRequestType type, IrcMessageEvent ircMessageEvent, CommandArgs commandArgs, Object... parameters) {
        this.type = type;
        this.ircMessageEvent = ircMessageEvent;
        this.commandArgs = commandArgs;
        this.parameters = parameters;
    }

    public ServiceRequestType getType() {
        return type;
    }

    public IrcMessageEvent getIrcMessageEvent() {
        return ircMessageEvent;
    }

    public CommandArgs getCommandArgs() {
        return commandArgs;
    }

    public Object[] getParameters() {
        return parameters;
    }

}
