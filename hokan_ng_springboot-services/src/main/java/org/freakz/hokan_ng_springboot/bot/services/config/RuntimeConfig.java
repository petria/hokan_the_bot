package org.freakz.hokan_ng_springboot.bot.services.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("file:hokan_runtime.properties")
@ConfigurationProperties(prefix = "hokan.service")
public class RuntimeConfig {

    //username=petria&userid=18436&handle=c11bc789efcd53818aedd7020c2dc5c6

    private String smsSendUsername;
    private String smsSendUserId;
    private String smsSendHandle;


    public String getSmsSendUsername() {
        return smsSendUsername;
    }

    public void setSmsSendUsername(String smsSendUsername) {
        this.smsSendUsername = smsSendUsername;
    }

    public String getSmsSendUserId() {
        return smsSendUserId;
    }

    public void setSmsSendUserId(String smsSendUserId) {
        this.smsSendUserId = smsSendUserId;
    }

    public String getSmsSendHandle() {
        return smsSendHandle;
    }

    public void setSmsSendHandle(String smsSendHandle) {
        this.smsSendHandle = smsSendHandle;
    }
}
