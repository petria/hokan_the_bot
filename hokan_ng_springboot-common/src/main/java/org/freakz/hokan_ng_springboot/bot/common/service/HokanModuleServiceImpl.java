package org.freakz.hokan_ng_springboot.bot.common.service;

import org.freakz.hokan_ng_springboot.bot.common.enums.HokanModule;
import org.freakz.hokan_ng_springboot.bot.common.jpa.entity.PropertyEntity;
import org.freakz.hokan_ng_springboot.bot.common.jpa.service.PropertyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Created by Petri Airio on 15.5.2015.
 */
@Component
@Scope("singleton")
public class HokanModuleServiceImpl implements HokanModuleService, CommandLineRunner {

    @Autowired
    private PropertyService propertyService;

    private long sessionId;

    @Value("${hokan.module}")
    private String hokanModule;

    private HokanModule module;

    //    @PostConstruct
    public void setHokanModule() {
        this.module = HokanModule.valueOf(hokanModule);
        this.sessionId = new Date().getTime();
        PropertyEntity property = propertyService.findFirstByPropertyName(module.getModuleProperty());
        if (property == null) {
            property = new PropertyEntity(module.getModuleProperty(), "", "");
        }
        property.setValue(this.sessionId + "");
        propertyService.save(property);
    }

    @Override
    public HokanModule getHokanModule() {
        return HokanModule.valueOf(hokanModule);
    }

    @Override
    public long getSessionId() {
        return this.sessionId;
    }

    @Override
    public void run(String... strings) throws Exception {
        setHokanModule();
    }
}
