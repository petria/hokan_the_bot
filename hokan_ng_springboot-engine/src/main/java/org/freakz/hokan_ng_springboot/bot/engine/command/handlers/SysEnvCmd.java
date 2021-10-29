package org.freakz.hokan_ng_springboot.bot.engine.command.handlers;

import com.martiansoftware.jsap.JSAPResult;
import org.freakz.hokan_ng_springboot.bot.common.events.EngineResponse;
import org.freakz.hokan_ng_springboot.bot.common.events.InternalRequest;
import org.freakz.hokan_ng_springboot.bot.common.exception.HokanException;
import org.freakz.hokan_ng_springboot.bot.common.jpa.entity.PropertyEntity;
import org.freakz.hokan_ng_springboot.bot.engine.command.HelpGroup;
import org.freakz.hokan_ng_springboot.bot.engine.command.annotation.HelpGroups;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by Petri Airio (petri.j.airio@gmail.com) on 26.8.2015.
 */
@Component
@Scope("prototype")
@HelpGroups(
        helpGroups = {HelpGroup.PROPERTIES, HelpGroup.SYSTEM}
)
public class SysEnvCmd extends Cmd {

    public SysEnvCmd() {
        super();
        setHelp("Shows system properties.");
        setAdminUserOnly(true);
    }

    @Override
    public void handleRequest(InternalRequest request, EngineResponse response, JSAPResult results) throws HokanException {

        List<PropertyEntity> propertyList = propertyService.findAll();
        for (PropertyEntity p : propertyList) {
            response.addResponse("%25s = %s\n", p.getPropertyName(), p.getValue());
        }
    }

}
