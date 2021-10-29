package org.freakz.hokan_ng_springboot.bot.engine.command.handlers;

import com.martiansoftware.jsap.JSAPResult;
import org.freakz.hokan_ng_springboot.bot.common.events.EngineResponse;
import org.freakz.hokan_ng_springboot.bot.common.events.InternalRequest;
import org.freakz.hokan_ng_springboot.bot.common.exception.HokanException;
import org.freakz.hokan_ng_springboot.bot.common.jpa.entity.Network;
import org.freakz.hokan_ng_springboot.bot.engine.command.HelpGroup;
import org.freakz.hokan_ng_springboot.bot.engine.command.annotation.HelpGroups;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * User: petria
 * Date: 1/8/14
 * Time: 5:37 PM
 *
 * @author Petri Airio <petri.j.airio@gmail.com>
 */
@Component
@Scope("prototype")

@HelpGroups(
        helpGroups = {HelpGroup.NETWORK, HelpGroup.SYSTEM}
)
public class NetworksCmd extends Cmd {

    public NetworksCmd() {
        super();
        setHelp("Shows configured IRC Networks");
    }

    @Override
    public void handleRequest(InternalRequest request, EngineResponse response, JSAPResult results) throws HokanException {
        StringBuilder sb = new StringBuilder();
        List<Network> nws = networkService.findAll();
        if (nws.size() > 0) {
            for (Network nw : nws) {
                sb.append("Name: ");
                sb.append(nw.getName());
                sb.append("\n");

                sb.append("  First connected : ");
                sb.append(nw.getFirstConnected());
                sb.append("\n");

                sb.append("  Connect count   : ");
                sb.append(nw.getConnectCount());
                sb.append("\n");

                sb.append("  Lines sent      : ");
                sb.append(nw.getLinesSent());
                sb.append("\n");

                sb.append("  Lines received  : ");
                sb.append(nw.getLinesReceived());
                sb.append("\n");

                sb.append("  Channels joined : ");
                sb.append(nw.getChannelsJoined());
                sb.append("\n");
            }

            response.addResponse(sb);
        }
    }

}
