package org.freakz.hokan_ng_springboot.bot.common.enums;

import org.freakz.hokan_ng_springboot.bot.common.jpa.entity.PropertyName;

/**
 * Created by Petri Airio on 9.4.2015.
 * -
 */
public enum HokanModule {

    HokanIo("HokanNGIoQueue", PropertyName.PROP_SYS_SESSION_ID_IO, false),
    HokanIoXMPP("HokanNGIoXMPPQueue", PropertyName.PROP_SYS_SESSION_ID_IO_XMPP, false),
    HokanIoTelegram("HokanNGIoTelegramQueue", PropertyName.PROP_SYS_SESSION_ID_IO_TELEGRAM, false),
    HokanEngine("HokanNGEngineQueue", PropertyName.PROP_SYS_SESSION_ID_ENGINE, false),
    HokanServices("HokanNGServicesQueue", PropertyName.PROP_SYS_SESSION_ID_SERVICES, false),
    HokanTestApp("HokanTestAppQueue", PropertyName.PROP_SYS_SESSION_ID_TEST, true),
    HokanUi("HokanNGUiQueue", PropertyName.PROP_SYS_SESSION_ID_UI, true);


    private final String queueName;
    private final PropertyName moduleProperty;
    private final boolean hidden;

    HokanModule(String queueName, PropertyName moduleProperty, boolean hidden) {
        this.queueName = queueName;
        this.moduleProperty = moduleProperty;
        this.hidden = hidden;
    }

    public String getQueueName() {
        return queueName;
    }

    public PropertyName getModuleProperty() {
        return moduleProperty;
    }

    public boolean isHidden() {
        return hidden;
    }

}
