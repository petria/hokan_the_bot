package org.freakz.hokan_ng_springboot.bot.common.jpa.entity;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Petri Airio on 23.2.2016.
 * -
 */
public enum UserFlag implements Serializable {

    ACTIVATED("AC", "User is activated"),
    ADMIN("AD", "User is admin"),
    IGNORE_ON_CHANNEL("IG", "User is ignored on public channels"),
    WEB_LOGIN("WL", "User can login via web ui");


    private final String shortName;

    private final String description;

    UserFlag(String shortName, String description) {
        this.shortName = shortName;
        this.description = description;
    }

    public static Set<UserFlag> getFlagSetFromString(String flagsString) {
        Set<UserFlag> flagsSet = new HashSet<>();
        if (flagsString == null || flagsString.length() == 0) {
            return flagsSet;
        }
        for (UserFlag flag : values()) {
            if (flagsString.contains(flag.getShortName())) {
                flagsSet.add(flag);
            }
        }
        return flagsSet;
    }

    public static Set<UserFlag> getFlagSetFromUser(User user) {
        return getFlagSetFromString(user.getFlags());
    }

    public static String getStringFromFlagSet(Set<UserFlag> set) {
        String setString = "";
        for (UserFlag flag : values()) {
            if (set.contains(flag)) {
                setString += String.format("%s:%s ", flag.getShortName(), flag.name());
            }
        }
        return setString.trim();
    }

    public static String getStringFromAllUserFlags() {
        String setString = "";
        for (UserFlag flag : values()) {
            setString += String.format("%s:%s ", flag.getShortName(), flag.name());
        }
        return setString.trim();
    }


    public static String getStringFromFlagSet(User user) {
        return getStringFromFlagSet(getFlagSetFromUser(user));
    }

    public static UserFlag getUserFlagFromString(String s) {
        for (UserFlag flag : values()) {
            if (s.equals(flag.getShortName())) {
                return flag;
            }
            if (s.equals(flag.name())) {
                return flag;
            }
            if (flag.name().toLowerCase().startsWith(s.toLowerCase())) {
                return flag;
            }
        }
        return null;
    }

    public String getShortName() {
        return shortName;
    }

    public String getDescription() {
        return description;
    }
}
