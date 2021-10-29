package org.freakz.hokan_ng_springboot.bot.common.jms;

import javax.jms.ObjectMessage;
import java.io.Serializable;

/**
 * Created by Petri Airio (petri.j.airio@gmail.com) on 14.4.2015.
 */
public class JmsEnvelope implements Serializable {

    private final ObjectMessage originalMessage;
    private final JmsMessage messageIn;
    private final JmsMessage messageOut;


    public JmsEnvelope(ObjectMessage originalMessage, JmsMessage messageIn, JmsMessage messageOut) {
        this.originalMessage = originalMessage;
        this.messageIn = messageIn;
        this.messageOut = messageOut;
    }

    public ObjectMessage getOriginalMessage() {
        return originalMessage;
    }

    public JmsMessage getMessageIn() {
        return messageIn;
    }

    public JmsMessage getMessageOut() {
        return messageOut;
    }

}
