package org.freakz.hokan_ng_springboot.bot.common.jms;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Petri Airio (petri.j.airio@gmail.com) 14/10/2016 / 12.10
 */
@Component
@Scope("singleton")
public class JmsStatsHandlerImpl implements JmsStatsHandler {

    private Map<String, Long> sentStats = new HashMap<>();
    private Map<String, Long> receivedStats = new HashMap<>();


    @Override
    public void messageSent(String target) {
        Long count = sentStats.get(target);
        if (count == null) {
            count = 0L;
        }
        count++;
        sentStats.put(target, count);
    }

    @Override
    public void messageReceived(String target) {
        Long count = receivedStats.get(target);
        if (count == null) {
            count = 0L;
        }
        count++;
        receivedStats.put(target, count);
    }


    public Map<String, Long> getSentStats() {
        return sentStats;
    }

    public Map<String, Long> getReceivedStats() {
        return receivedStats;
    }

}
