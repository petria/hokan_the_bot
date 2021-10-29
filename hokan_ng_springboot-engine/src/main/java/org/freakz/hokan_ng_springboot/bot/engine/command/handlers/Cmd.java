package org.freakz.hokan_ng_springboot.bot.engine.command.handlers;

import com.martiansoftware.jsap.*;
import org.freakz.hokan_ng_springboot.bot.common.cmdpool.CommandPool;
import org.freakz.hokan_ng_springboot.bot.common.cmdpool.CommandRunnable;
import org.freakz.hokan_ng_springboot.bot.common.enums.HokanModule;
import org.freakz.hokan_ng_springboot.bot.common.events.*;
import org.freakz.hokan_ng_springboot.bot.common.exception.HokanEngineException;
import org.freakz.hokan_ng_springboot.bot.common.exception.HokanException;
import org.freakz.hokan_ng_springboot.bot.common.jms.JmsEnvelope;
import org.freakz.hokan_ng_springboot.bot.common.jms.JmsMessage;
import org.freakz.hokan_ng_springboot.bot.common.jms.api.JmsSender;
import org.freakz.hokan_ng_springboot.bot.common.jpa.service.*;
import org.freakz.hokan_ng_springboot.bot.common.service.AccessControlService;
import org.freakz.hokan_ng_springboot.bot.common.service.HokanStatusService;
import org.freakz.hokan_ng_springboot.bot.common.service.StatsService;
import org.freakz.hokan_ng_springboot.bot.common.service.SystemScriptRunnerService;
import org.freakz.hokan_ng_springboot.bot.common.util.CommandArgs;
import org.freakz.hokan_ng_springboot.bot.engine.command.HelpGroup;
import org.freakz.hokan_ng_springboot.bot.engine.command.annotation.HelpGroups;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import java.lang.annotation.Annotation;
import java.util.*;

/**
 * User: petria
 * Date: 11/6/13
 * Time: 4:02 PM
 *
 * @author Petri Airio <petri.j.airio@gmail.com>
 */
@SuppressWarnings("unchecked")
public abstract class Cmd implements HokanCommand, CommandRunnable {

    private static final Logger log = LoggerFactory.getLogger(Cmd.class);

    @Autowired
    protected AccessControlService accessControlService;

    @Autowired
    protected AliasService aliasService;

    @Autowired
    protected ChannelService channelService;

    @Autowired
    protected ChannelStatsService channelStatsService;

    @Autowired
    protected ChannelPropertyService channelPropertyService;

    @Autowired
    protected CommandPool commandPool;

    @Autowired
    protected DataValuesService dataValuesService;

    @Autowired
    protected JoinedUserService joinedUsersService;

    @Autowired
    protected NetworkService networkService;

    @Autowired
    protected PropertyService propertyService;

    @Autowired
    protected SystemScriptRunnerService scriptRunnerService;

    @Autowired
    protected SearchReplaceService searchReplaceService;

    @Autowired
    protected HokanStatusService statusService;

    @Autowired
    protected StatsService statsService;

    @Autowired
    protected UrlLoggerService urlLoggerService;

    @Autowired
    protected UserService userService;

    @Autowired
    protected UserChannelService userChannelService;

    @Autowired
    private JmsSender jmsSender;

    @Autowired
    protected ApplicationContext context;

    protected JSAP jsap = new JSAP();

    protected boolean broken;

    protected boolean channelOpOnly;

    protected boolean loggedInOnly;

    protected boolean channelOnly;

    protected boolean privateOnly;

    protected boolean adminUserOnly;

    protected boolean toBotOnly;

    protected boolean noWeb;

    protected boolean isChannelOp;

    protected boolean isLoggedIn;

    protected boolean isPublic;

    protected boolean isPrivate;

    protected boolean isAdminUser;

    protected boolean isToBot;

    private String helpWikiUrl;

    public Cmd() {
    }

    protected void registerParameter(Parameter parameter) {
        try {
            jsap.registerParameter(parameter);
        } catch (JSAPException e) {
            log.error("Error registering command parameter", e);
        }
    }

    public String getMatchPattern() {
        return String.format("!%s", getName().toLowerCase());
    }

    public String getName() {
        String name = this.getClass().getSimpleName();
        if (name.endsWith("Cmd")) {
            name = name.replaceAll("Cmd", "");
        }
        return name;
    }

    @Override
    public String getExample() {
        return null;
    }

    protected void setBroken(boolean b) {
        this.broken = b;
    }

    protected String buildSeeAlso(Cmd cmd) {

        Comparator<Cmd> comparator = (cmd1, cmd2) -> cmd1.getName().compareTo(cmd2.getName());

        String seeAlsoGroups = "";
        for (HelpGroup group : getCmdHelpGroups(cmd)) {
            List<Cmd> groupCmds = getOtherCmdsInGroup(group);
            Collections.sort(groupCmds, comparator);
            if (groupCmds.size() > 0) {
                for (Cmd groupCmd : groupCmds) {
                    if (groupCmd.getName().equals(cmd.getName())) {
                        continue;
                    }
                    seeAlsoGroups += " " + groupCmd.getName();
                }
            }
        }
        String seeAlsoHelp = "";
        if (seeAlsoGroups.length() > 0) {
            seeAlsoHelp = "\nSee also :" + seeAlsoGroups;
        }
        return seeAlsoHelp;
    }

    protected List<Cmd> getOtherCmdsInGroup(HelpGroup group) {
        List<Cmd> other = new ArrayList<>();
        for (Cmd theCmd : context.getBeansOfType(Cmd.class).values()) {
            Class obj = theCmd.getClass();
            if (obj.isAnnotationPresent(HelpGroups.class)) {
                Annotation annotation = obj.getAnnotation(HelpGroups.class);
                HelpGroups helpGroups = (HelpGroups) annotation;
                HelpGroup[] groups = helpGroups.helpGroups();
                for (HelpGroup cmdGroup : groups) {
                    if (cmdGroup == group) {
                        other.add(theCmd);
                    }
                }
            }
        }
        return other;
    }

    protected HelpGroup[] getCmdHelpGroups(Cmd cmd) {
        Class obj = cmd.getClass();
        if (obj.isAnnotationPresent(HelpGroups.class)) {
            Annotation annotation = obj.getAnnotation(HelpGroups.class);
            HelpGroups helpGroups = (HelpGroups) annotation;
            HelpGroup[] groups = helpGroups.helpGroups();
            return groups;
        }
        return new HelpGroup[0];
    }

    public void setHelpWikiUrl(String url) {
        this.helpWikiUrl = url;
    }

    public void handleLine(InternalRequest request, EngineResponse response) {
        IrcMessageEvent ircEvent = request.getIrcEvent();
        CommandArgs args = new CommandArgs(ircEvent.getMessage());

        response.setCommandClass(this.getClass().toString());
        if (!checkAccess(request, response)) {
            log.debug("Access denied user: {} - command {}", request.getUser(), this);
            sendReply(response, request.getJmsEnvelope());
            return;
        }
        if (broken) {
            log.debug("{} marked broken, not executing!", this.getName());
            response.setResponseMessage("Command currently broken / does not work!");
            sendReply(response, request.getJmsEnvelope());
            return;
        }
        if (args.hasArgs() && args.getArgs().equals("?")) {
            StringBuilder sb = new StringBuilder();
            String usage = "!" + this.getName().toLowerCase() + " " + this.jsap.getUsage();
            String help = this.jsap.getHelp();
            sb.append("Usage    : ");
            sb.append(usage);
            sb.append("\n");
            String example = this.getExample();
            if (example != null) {
                sb.append("Example  : ");
                sb.append(example);
                sb.append("\n");

            }
            sb.append("Help     : ");
            sb.append(help);
            sb.append("\n");
            if (getHelpWikiUrl() != null && getHelpWikiUrl().length() > 0) {
                sb.append("Wiki URL : ");
                sb.append(getHelpWikiUrl());
                sb.append("\n");
            }
            sb.append(buildSeeAlso(this));

            response.setResponseMessage(sb.toString());
            sendReply(response, request.getJmsEnvelope());
        } else {

            boolean parseRes;
            JSAPResult results = null;
            IDMap map = jsap.getIDMap();
            Iterator iterator = map.idIterator();
            String argsLine = args.joinArgs(1);
            if (iterator.hasNext()) {
                results = jsap.parse(argsLine);
                parseRes = results.success();
            } else {
                parseRes = true;
            }

            if (!parseRes) {
                response.setResponseMessage(getUsage());
                sendReply(response, request.getJmsEnvelope());
            } else {

                ArgsWrapper wrapper = new ArgsWrapper();
                wrapper.request = request;
                wrapper.response = response;
                wrapper.results = results;
                commandPool.startRunnable(this, request.getUser().getNick(), wrapper);
            }
        }
    }

    public String getUsage() {
        return String.format("Invalid arguments, usage: %s %s", getName(), jsap.getUsage());
    }

    public String getHelpWikiUrl() {
        return this.helpWikiUrl;
    }

    @Override
    public void handleRun(long myPid, Object args) throws HokanException {
        ArgsWrapper wrapper = (ArgsWrapper) args;
        try {
            handleRequest(wrapper.request, wrapper.response, wrapper.results);
        } catch (Exception e) {
            HokanEngineException engineException = new HokanEngineException(e, getClass().getName());
            wrapper.response.setException(engineException);
            log.error("Command handler returned exception {}", e);
        }
        sendReply(wrapper.response, wrapper.request.getJmsEnvelope());
    }

    private void sendReply(EngineResponse response, JmsEnvelope envelope) {
        //    log.debug("Sending response: {}", response);
        if (response.isEngineRequest()) {

            envelope.getMessageOut().addPayLoadObject("ENGINE_RESPONSE", response.getResponseMessage());
        } else {
            jmsSender.send(HokanModule.HokanEngine, envelope.getMessageIn().getSender().getQueueName(), "ENGINE_RESPONSE", response, false);
        }
    }

    public abstract void handleRequest(InternalRequest request, EngineResponse response, JSAPResult results) throws HokanException;

    private boolean checkAccess(InternalRequest request, EngineResponse response) {

        IrcMessageEvent ircMessageEvent = request.getIrcEvent();
        isLoggedIn = accessControlService.isLoggedIn(request.getUser());
        isPublic = !ircMessageEvent.isPrivate();
        isPrivate = ircMessageEvent.isPrivate();
        isToBot = ircMessageEvent.isToMe();
        isAdminUser = accessControlService.isAdminUser(request.getUser());
        isChannelOp = accessControlService.isChannelOp(request.getUser(), request.getChannel());

        boolean ret = true;

        if (isLoggedInOnly() && !isLoggedIn && !isAdminUser) {
            response.setResponseMessage("LoggedIn only: " + getName());
            response.setReplyTo(ircMessageEvent.getSender());
            ret = false;
        }

        if (isToBotOnly() && !isToBot && !isAdminUser) {
            response.setResponseMessage("Can be used via message to Bot only: " + getName());
            response.setReplyTo(ircMessageEvent.getSender());
            ret = false;
        }

        if (isChannelOnly() && !isPublic && !isAdminUser) {
            response.setResponseMessage("Can be used via channel messages only: " + getName());
            response.setReplyTo(ircMessageEvent.getSender());
            ret = false;
        }

        if (isPrivateOnly() && isPublic && !isAdminUser) {
            response.setResponseMessage("Can be used via private messages only: " + getName());
            response.setReplyTo(ircMessageEvent.getSender());
            ret = false;
        }

        if (isChannelOpOnly() && !isChannelOp && !isAdminUser) {
            response.setResponseMessage("ChannelOp only: " + getName());
            response.setReplyTo(ircMessageEvent.getSender());
            ret = false;
        }

        if (isAdminUserOnly() && !isAdminUser) {
            response.setResponseMessage("Admin user only: " + getName());
            response.setReplyTo(ircMessageEvent.getSender());
            ret = false;
        }
        return ret;
    }

    public boolean isNoWeb() {
        return noWeb;
    }

    public void setNoWeb(boolean noWeb) {
        this.noWeb = noWeb;
    }

    public boolean isChannelOpOnly() {
        return channelOpOnly;
    }

    public void setChannelOpOnly(boolean channelOpOnly) {
        this.channelOpOnly = channelOpOnly;
    }

    // ---------------------

    public boolean isLoggedInOnly() {
        return loggedInOnly;
    }

    public void setLoggedInOnly(boolean loggedInOnly) {
        this.loggedInOnly = loggedInOnly;
    }

    public boolean isChannelOnly() {
        return channelOnly;
    }

    public void setChannelOnly(boolean channelOnly) {
        this.channelOnly = channelOnly;
    }

    public boolean isPrivateOnly() {
        return privateOnly;
    }

    public void setPrivateOnly(boolean privateOnly) {
        this.privateOnly = privateOnly;
    }

    public boolean isAdminUserOnly() {
        return this.adminUserOnly;
    }

    public void setAdminUserOnly(boolean adminUserOnly) {
        this.adminUserOnly = adminUserOnly;
    }

    public boolean isToBotOnly() {
        return toBotOnly;
    }

    public void setToBotOnly(boolean toBotOnly) {
        this.toBotOnly = toBotOnly;
    }

    public String getHelp() {
        return this.jsap.getHelp();
    }

    public void setHelp(String helpText) {
        this.jsap.setHelp(helpText);
    }

    public static class ArgsWrapper {

        public InternalRequest request;

        public EngineResponse response;

        public JSAPResult results;
    }


    public ServiceResponse doServicesRequest(ServiceRequestType requestType, IrcMessageEvent ircEvent, Object... parameters) throws HokanException {
        ServiceRequest request = new ServiceRequest(requestType, ircEvent, new CommandArgs(ircEvent.getMessage()), parameters);
        ObjectMessage objectMessage = jmsSender.sendAndGetReply(HokanModule.HokanServices.getQueueName(), "SERVICE_REQUEST", request, false);
        if (objectMessage == null) {
            throw new HokanException("ServiceResponse null, is Services module running?");
        }
        try {
            JmsMessage jmsMessage = (JmsMessage) objectMessage.getObject();
            return jmsMessage.getServiceResponse();
        } catch (JMSException e) {
            log.error("jms", e);
        }
        return new ServiceResponse(requestType);
    }

}
