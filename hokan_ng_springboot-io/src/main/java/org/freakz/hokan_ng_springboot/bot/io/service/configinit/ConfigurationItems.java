package org.freakz.hokan_ng_springboot.bot.io.service.configinit;

/**
 * Created by Petri Airio on 7.3.2017.
 */
public enum ConfigurationItems {
    BOT_NAME("Bot name", "HokanBot"),
    NETWORK_NAME("Network name", "HokanIrcNET"),

    IRC_SERVER_ADDRESS("IRC server address", "irc.inet.fi"),
    IRC_SERVER_PORT("IRC server port", "6667"),
    IRC_SERVER_PASSWORD("IRC server password", ""),

    CHANNELS_TO_JOIN("Channels to join", "#HokanDEV,#HokanDEV2");

    private final String description;
    private final String defaults;

    ConfigurationItems(String description, String defaults) {
        this.description = description;
        this.defaults = defaults;
    }

    public String getDescription() {
        return description;
    }

    public String getDefaults() {
        return defaults;
    }
}
