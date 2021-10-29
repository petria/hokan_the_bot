package org.freakz.hokan_ng_springboot.bot.engine.command.handlers;

import org.freakz.hokan_ng_springboot.bot.common.enums.HokanModule;
import org.freakz.hokan_ng_springboot.bot.common.events.IrcMessageEvent;
import org.freakz.hokan_ng_springboot.bot.common.events.ServiceRequest;
import org.freakz.hokan_ng_springboot.bot.common.events.ServiceRequestType;
import org.freakz.hokan_ng_springboot.bot.common.events.ServiceResponse2;
import org.freakz.hokan_ng_springboot.bot.common.exception.HokanException;
import org.freakz.hokan_ng_springboot.bot.common.jms.JmsMessage2;
import org.freakz.hokan_ng_springboot.bot.common.jms.api.JmsSender;
import org.freakz.hokan_ng_springboot.bot.common.util.CommandArgs;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;

//@Slf4j
public class DoServiceRequest<T> {

    public T doServicesRequest(ServiceRequestType requestType, IrcMessageEvent ircEvent, Object... parameters) throws HokanException {
        ServiceRequest request = new ServiceRequest(requestType, ircEvent, new CommandArgs(ircEvent.getMessage()), parameters);
        ObjectMessage objectMessage = BeanUtil.getBean(JmsSender.class).sendAndGetReply(HokanModule.HokanServices.getQueueName(), "SERVICE_REQUEST", request, false);
        if (objectMessage == null) {
            throw new HokanException("ServiceResponse null, is Services module running?");
        }
        try {
            JmsMessage2 jmsMessage = (JmsMessage2) objectMessage.getObject();
            ServiceResponse2<T> serviceResponse = jmsMessage.getServiceResponse();
            return serviceResponse.getResponse();
        } catch (JMSException e) {
            //          log.error("jms", e);
        }
        return null;
    }

}
