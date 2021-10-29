package org.freakz.hokan_ng_springboot.bot.engine.service;

import org.freakz.hokan_ng_springboot.bot.engine.command.handlers.Cmd;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Petri Airio on 4.2.2016.
 * -
 */
public class CmdHandlerMatches implements Serializable {

    private final List<Cmd> matches;
    private final String firstWord;

    public CmdHandlerMatches(String firstWord) {
        this.firstWord = firstWord;
        this.matches = new ArrayList<>();
    }

    public List<Cmd> getMatches() {
        return matches;
    }

    public String getFirstWord() {
        return firstWord;
    }

}
