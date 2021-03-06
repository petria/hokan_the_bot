package org.freakz.hokan_ng_springboot.bot.services.service.topcounter;

import lombok.extern.slf4j.Slf4j;
import org.freakz.hokan_ng_springboot.bot.common.enums.HokanModule;
import org.freakz.hokan_ng_springboot.bot.common.enums.TopCountsEnum;
import org.freakz.hokan_ng_springboot.bot.common.events.IrcMessageEvent;
import org.freakz.hokan_ng_springboot.bot.common.events.NotifyRequest;
import org.freakz.hokan_ng_springboot.bot.common.events.ServiceRequest;
import org.freakz.hokan_ng_springboot.bot.common.events.ServiceRequestType;
import org.freakz.hokan_ng_springboot.bot.common.events.ServiceResponse;
import org.freakz.hokan_ng_springboot.bot.common.jms.api.JmsSender;
import org.freakz.hokan_ng_springboot.bot.common.jpa.service.DataValuesService;
import org.freakz.hokan_ng_springboot.bot.common.models.DataValuesModel;
import org.freakz.hokan_ng_springboot.bot.common.util.StringStuff;
import org.freakz.hokan_ng_springboot.bot.services.service.annotation.ServiceMessageHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
@Slf4j
public class TopCountService {

    private final DataValuesService dataValuesService;
    private final JmsSender jmsSender;

    @Autowired
    public TopCountService(DataValuesService dataValuesService, JmsSender jmsSender) {
        this.dataValuesService = dataValuesService;
        this.jmsSender = jmsSender;
    }

    @ServiceMessageHandler(ServiceRequestType = ServiceRequestType.TOP_COUNT_REQUEST)
    public void calculateTopCounters(ServiceRequest request, ServiceResponse response) {

        for (TopCountsEnum countEnum : TopCountsEnum.values()) {
            if (doCalc(request, countEnum)) {
                handleLastTime(request, countEnum);
            }
        }
    }

    private void handleLastTime(ServiceRequest request, TopCountsEnum countEnum) {
        String nick = request.getIrcMessageEvent().getSender().toLowerCase();
        String channel = request.getIrcMessageEvent().getChannel().toLowerCase();
        String network = request.getIrcMessageEvent().getNetwork().toLowerCase();
        String key = String.format(countEnum.getLastTimeKeyName(), nick.toUpperCase());
        dataValuesService.setValue(nick, channel, network, key, System.currentTimeMillis() + "");
    }

    //                              0     1     2     3     4     5     6
    private static int[] COUNTS = {500, 1000, 1024, 2000, 3000, 4000, 10000};
    private static Map<Integer, String[]> COUNT_MSGS = new HashMap<>();

    static {
        COUNT_MSGS.put(COUNTS[0], new String[]{"Mi Frend-l??pp??", "Mi Frend-l??pp??", "Mi Frend-l??pp??"});
        COUNT_MSGS.put(COUNTS[1], new String[]{"Yor Mi Frend-l??pp??", "Yor Mi Frend-l??pp??", "Yor Mi Frend-l??pp??"});
        COUNT_MSGS.put(COUNTS[2], new String[]{"2^10", "1k", "2^10"});
        COUNT_MSGS.put(COUNTS[3], new String[]{"3 msg1", "3 msg2", "3 msg3"});
        COUNT_MSGS.put(COUNTS[4], new String[]{"4 msg1", "4 msg2", "4 msg3"});
        COUNT_MSGS.put(COUNTS[5], new String[]{"5 msg1", "5 msg2", "5 msg3"});
        COUNT_MSGS.put(COUNTS[6], new String[]{"6 msg1", "6 msg2", "6 msg3"});
    }


    private boolean doCalc(ServiceRequest request, TopCountsEnum countEnum) {
        String message = request.getIrcMessageEvent().getMessage().toLowerCase();
        if (message.matches(countEnum.getRegex())) {
            String nick = request.getIrcMessageEvent().getSender().toLowerCase();
            String channel = request.getIrcMessageEvent().getChannel().toLowerCase();
            String network = request.getIrcMessageEvent().getNetwork().toLowerCase();

//            log.debug("Got  {} from: {}", key, nick);

            String keyWithTime = buildKeyWithTimeInfo(request.getIrcMessageEvent(), countEnum.getKeyName());

            String value = dataValuesService.getValue(nick, channel, network, keyWithTime);
            if (value == null) {
                value = "1";
            } else {
                int count = Integer.parseInt(value);
                count++;
                value = "" + count;

                for (int limit : COUNTS) {
                    if (count == limit) {
                        String rndMsg = StringStuff.getRandomString(COUNT_MSGS.get(limit));
                        IrcMessageEvent iEvent = request.getIrcMessageEvent();
                        String positionChange = String.format("%s: %d. -> %s!!", countEnum.getPrettyName(), count, rndMsg);
                        processReply(iEvent, iEvent.getSender() + ": " + positionChange);
                    }
                }

            }

            PositionChange oldPos = getNickPosition(channel, network, countEnum.getKeyName(), nick);

//            log.debug("{} {} count: {}", key, nick, value);
            dataValuesService.setValue(nick, channel, network, keyWithTime, value);

            PositionChange newPos = getNickPosition(channel, network, countEnum.getKeyName(), nick);

//            log.debug("key: {} - oldPos: {} <-> newPos {}", key, oldPos, newPos);

            if (oldPos != null && newPos != null) {
                if (newPos.position < oldPos.position) {

                    String posText;
                    if (newPos.position == 1) {
                        posText = String.format("*%s*  \u0002%d. you = %s\u0002 <--> %d. %s = %s",
                                countEnum.getPrettyName(),
                                newPos.position,
                                newPos.own.getValue(),
                                newPos.position + 1,
                                newPos.after.getNick(),
                                newPos.after.getValue()
                        );

                    } else {
                        posText = String.format("*%s*  %d. %s = %s <--> \u0002%d. you = %s\u0002 <--> %d. %s = %s",
                                countEnum.getPrettyName(),
                                newPos.position - 1,
                                newPos.ahead.getNick(),
                                newPos.ahead.getValue(),
                                newPos.position,
                                newPos.own.getValue(),
                                newPos.position + 1,
                                newPos.after.getNick(),
                                newPos.after.getValue()
                        );

                    }
                    IrcMessageEvent iEvent = request.getIrcMessageEvent();
//                    String positionChange = String.format("%s: %d. -> %d. !!", countEnum.getKeyName(), oldPos.position, newPos.position);
                    processReply(iEvent, iEvent.getSender() + ": " + posText);
                }
            }
            return true;
        }
        return false;
    }

    private String buildKeyWithTimeInfo(IrcMessageEvent ircMessageEvent, String key) {
        LocalDateTime ts = ircMessageEvent.getTimeStamp();
        String keyWithTimeInfo
                = String.format("%s_%d_%02d_%02d_%02d",
                key,
                ts.getYear(),
                ts.getDayOfMonth(),
                ts.getMonthValue(),
                ts.getHour()
        );

        return keyWithTimeInfo;
    }

    private void processReply(IrcMessageEvent iEvent, String reply) {

        NotifyRequest notifyRequest = new NotifyRequest();
        notifyRequest.setNotifyMessage(reply);
        notifyRequest.setTargetChannelId(iEvent.getChannelId());
        jmsSender.send(HokanModule.HokanServices, HokanModule.HokanIo.getQueueName(), "WHOLE_LINE_TRIGGER_NOTIFY_REQUEST", notifyRequest, false);
    }

    class PositionChange {
        DataValuesModel ahead = null;
        DataValuesModel own = null;
        int position = 0;
        DataValuesModel after = null;
    }

    private PositionChange getNickPosition(String channel, String network, String key, String nick) {
        List<DataValuesModel> dataValues = dataValuesService.getDataValuesAsc(channel, network, key);
        if (dataValues.size() > 0) {

            for (int i = 0; i < dataValues.size(); i++) {
                if (dataValues.get(i).getNick().equalsIgnoreCase(nick)) {
                    PositionChange change = new PositionChange();
                    change.own = dataValues.get(i);
                    change.position = i + 1;
                    if (i > 0) {
                        change.ahead = dataValues.get(i - 1);
                    }
                    if (i != dataValues.size() - 1) {
                        change.after = dataValues.get(i + 1);
                    }
                    return change;
                }
            }
        }
        return null;
    }

}
