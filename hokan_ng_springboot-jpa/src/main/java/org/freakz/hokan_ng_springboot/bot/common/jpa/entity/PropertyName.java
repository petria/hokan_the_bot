package org.freakz.hokan_ng_springboot.bot.common.jpa.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * User: petria
 * Date: 11/7/13
 * Time: 5:59 PM
 *
 * @author Petri Airio <petri.j.airio@gmail.com>
 */
public enum PropertyName {


    PROP_SYS_ACCESS_CONTROL("sys.AccessControl"),
    PROP_SYS_ADMIN_USER_TOKEN("sys.AdminUserToken"),
    PROP_SYS_BOT_NICK("sys.BotNick"),
    PROP_SYS_CONFIRM_LONG_MESSAGES("sys.ConfirmLongMessages"),
    PROP_SYS_DAY_CHANGED_TIME("sys.DayChangedTime"),
    PROP_SYS_GOOGLE_API_KEY("sys.GoogleApiKey"),
    PROP_SYS_EXEC("sys.Exec"),
    PROP_SYS_EXT_TITLE_RESOLVER("sys.TitleResolver"),
    PROP_SYS_HTTP_PROXY_HOST("sys.HttpProxyHost"),
    PROP_SYS_HTTP_PROXY_PORT("sys.HttpProxyPort"),
    PROP_SYS_HTTP_USER_AGENT("sys.HttpUserAgent"),
    PROP_SYS_IGNORE("sys.Ignore"),
    PROP_SYS_MAX_CONNECTION_RETRY("sys.MaxConnectionRetry"),
    PROP_SYS_PID_COUNTER("sys.PidCounter"),
    PROP_SYS_RAWLOG("sys.RawLog"),
    PROP_SYS_SESSION_ID_ENGINE("sys.SessionIdEngine"),
    PROP_SYS_SESSION_ID_IO("sys.SessionIdIo"),
    PROP_SYS_SESSION_ID_IO_XMPP("sys.SessionIdIoXmpp"),
    PROP_SYS_SESSION_ID_IO_TELEGRAM("sys.SessionIdIoTelegram"),
    PROP_SYS_SESSION_ID_SERVICES("sys.SessionIdServices"),
    PROP_SYS_SESSION_ID_TEST("sys.SessionIdTest"),
    PROP_SYS_SESSION_ID_UI("sys.SessionIdUi"),
    PROP_SYS_TV_XML_DATA_WATCH_DIR("sys.TvXmlDataWatchDir"),
    PROP_SYS_YULE_TIME("sys.YuleTime"),
    PROP_SYS_DO_WEB_COMMANDS("sys.DoWebCommands"),

    PROP_CHANNEL_BB_MODE("channel.BBMode"),
    PROP_CHANNEL_TELEGRAM_LINK("channel.TelegramLink"),
    PROP_CHANNEL_DO_DAY_CHANGED("channel.DoDayChanged"),
    PROP_CHANNEL_DO_JOIN_MESSAGE("channel.DoJoinMessage"),
    PROP_CHANNEL_DO_KICK_REJOIN("channel.DoKickRejoin"),
    PROP_CHANNEL_DO_KORONA("channel.DoKorona"),
    PROP_CHANNEL_DO_TVNOTIFY("channel.DoNotify"),
    PROP_CHANNEL_DO_SEARCH_REPLACE("channel.DoSearchReplace"),
    PROP_CHANNEL_DO_STATS("channel.DoStats"),
    PROP_CHANNEL_DO_TORRENTS("channel.DoTorrents"),
    PROP_CHANNEL_DO_URL_TITLES("channel.DoUrlTitles"),
    PROP_CHANNEL_DO_YULE("channel.DoYule"),
    PROP_CHANNEL_DO_WHOLELINE_TRICKERS("channel.DoWholeLineTrickers");

    private final String text;

    /**
     * @param text property name
     */
    PropertyName(String text) {
        this.text = text;
    }

    /* (non-Javadoc)
     * @see java.lang.Enum#toString()
     */
    @Override
    public String toString() {
        return text;
    }

    public static List<PropertyName> getValuesLike(String pattern) {
        List<PropertyName> values = new ArrayList<>();
        for (PropertyName name : values()) {
            if (name.toString().toLowerCase().matches(pattern.toLowerCase())) {
                values.add(name);
            }
        }
        return values;
    }
}
