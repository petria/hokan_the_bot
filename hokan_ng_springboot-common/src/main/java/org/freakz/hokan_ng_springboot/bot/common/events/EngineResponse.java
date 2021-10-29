package org.freakz.hokan_ng_springboot.bot.common.events;

import org.freakz.hokan_ng_springboot.bot.common.jms.JmsEnvelope;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * User: petria
 * Date: 11/6/13
 * Time: 10:42 AM
 *
 * @author Petri Airio <petri.j.airio@gmail.com>
 */
public class EngineResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private int responseStatus;
    private String responseMessage = "";
    private Throwable exception;
    private String replyTo;
    private String commandClass;
    private boolean noSearchReplace;
    private boolean isEngineRequest;
    private JmsEnvelope jmsEnvelope;

    private List<EngineMethodCall> engineMethodCalls = new ArrayList<>();
    private IrcMessageEvent ircMessageEvent;

    public EngineResponse() {
    }

    public EngineResponse(IrcMessageEvent ircMessageEvent) {
        this.ircMessageEvent = ircMessageEvent;
        if (ircMessageEvent.isPrivate()) {
            replyTo = ircMessageEvent.getSender();
        } else {
            replyTo = ircMessageEvent.getChannel();
        }
    }

    public int getResponseStatus() {
        return responseStatus;
    }

    public void setResponseStatus(int responseStatus) {
        this.responseStatus = responseStatus;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }


    public void addEngineMethodCall(String methodName, String... methodArgs) {
        this.engineMethodCalls.add(new EngineMethodCall(methodName, methodArgs));
    }

    public List<EngineMethodCall> getEngineMethodCalls() {
        return engineMethodCalls;
    }

    public void setEngineMethodCalls(List<EngineMethodCall> engineMethodCalls) {
        this.engineMethodCalls = engineMethodCalls;
    }

    public Throwable getException() {
        return exception;
    }

    public void setException(Throwable exception) {
        this.exception = exception;
    }

    public String getReplyTo() {
        return replyTo;
    }

    public void setReplyTo(String replyTo) {
        this.replyTo = replyTo;
    }

    public void addResponse(String response) {
        this.responseMessage += response;
    }

    public void addResponse(String response, Object... args) {
        this.responseMessage += String.format(response, (Object[]) args);
    }

    public void addResponse(StringBuilder sb, Object... args) {
        addResponse(sb.toString(), args);
    }

    public String getCommandClass() {
        return commandClass;
    }

    public void setCommandClass(String commandClass) {
        this.commandClass = commandClass;
    }

    public boolean isNoSearchReplace() {
        return noSearchReplace;
    }

    public void setNoSearchReplace(boolean noSearchReplace) {
        this.noSearchReplace = noSearchReplace;
    }

    public boolean isEngineRequest() {
        return isEngineRequest;
    }

    public void setIsEngineRequest(boolean isEngineRequest) {
        this.isEngineRequest = isEngineRequest;
    }

    public IrcMessageEvent getIrcMessageEvent() {
        return this.ircMessageEvent;
    }

    @Override
    public String toString() {
        return String.format("\nreplyTo: %s\nresponseMessage: %s", replyTo, responseMessage);
    }

}
