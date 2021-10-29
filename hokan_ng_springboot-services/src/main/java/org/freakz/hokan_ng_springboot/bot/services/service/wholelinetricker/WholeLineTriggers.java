package org.freakz.hokan_ng_springboot.bot.services.service.wholelinetricker;

import org.freakz.hokan_ng_springboot.bot.common.events.IrcMessageEvent;

public interface WholeLineTriggers {

    void checkWholeLineTrigger(IrcMessageEvent iEvent);

}
