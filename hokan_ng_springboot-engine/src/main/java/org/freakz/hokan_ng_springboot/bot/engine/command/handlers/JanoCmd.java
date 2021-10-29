package org.freakz.hokan_ng_springboot.bot.engine.command.handlers;

import com.martiansoftware.jsap.JSAPResult;
import org.freakz.hokan_ng_springboot.bot.common.events.EngineResponse;
import org.freakz.hokan_ng_springboot.bot.common.events.InternalRequest;
import org.freakz.hokan_ng_springboot.bot.common.exception.HokanException;
import org.freakz.hokan_ng_springboot.bot.common.util.Uptime;
import org.freakz.hokan_ng_springboot.bot.engine.command.HelpGroup;
import org.freakz.hokan_ng_springboot.bot.engine.command.annotation.HelpGroups;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
@HelpGroups(
        helpGroups = {HelpGroup.DATA_COLLECTION}
)
public class JanoCmd extends Cmd {

    public JanoCmd() {
        setHelp("How thirsty you are!? When did you last *glugga* !??!");
    }

    @Override
    public void handleRequest(InternalRequest request, EngineResponse response, JSAPResult results) throws HokanException {
        String nick = request.getIrcEvent().getSender().toLowerCase();
        String channel = request.getIrcEvent().getChannel().toLowerCase();
        String network = request.getIrcEvent().getNetwork().toLowerCase();
        String key = String.format("%s_LAST_GLUGGA", nick.toUpperCase());
        String value = dataValuesService.getValue(nick, channel, network, key);
        if (value != null) {
            Uptime uptime = new Uptime(Long.parseLong(value));
            long future = System.currentTimeMillis(); // + 143434344343L;
            Integer[] td = uptime.getTimeDiff(future);
            String days = getValue(td[3], 0, "day");
            String hours = getValue(td[2], 0, "hour");
            String minutes = getValue(td[1], 0, "minute");
            String second = getValue(td[0], -1, "second");

            String noGlugga = String.format("%s%s%s%s", days, hours, minutes, second);
            response.addResponse("Your last *glugga*: %sago!!", noGlugga);

        } else {
            response.addResponse("No *glugga* no fun!");
        }
    }

    private String getValue(Integer value, int compare, String str) {
        String ret = "";
        if (value > compare) {
            String many = value > 1 ? "s" : "";
            ret = String.format("%d %s%s ", value, str, many);
        }
        return ret;
    }
}
