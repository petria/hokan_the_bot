package org.freakz.hokan_ng_springboot.bot.common.jms.api;

import org.freakz.hokan_ng_springboot.bot.common.jms.JmsEnvelope;

/**
 * Created by petria on 10.2.2015.
 */
public interface JmsServiceMessageHandler {

    void handleJmsEnvelope(JmsEnvelope envelope) throws Exception;

}
