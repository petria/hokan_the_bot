package org.freakz.hokan_ng_springboot.bot.common.models;

import java.io.Serializable;

/**
 * Created by Petri Airio on 24.8.2015.
 */
public class StatsData implements Serializable {

    private String nick;
    private int words;
    private int lines;


    public StatsData(String nickLower) {
        this.nick = nickLower;
        this.words = 0;
        this.lines = 0;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public void addToWords(int amount) {
        this.words += amount;
    }

    public int getWords() {
        return words;
    }

    public void setWords(int words) {
        this.words = words;
    }

    public void addToLines(int amount) {
        this.lines += amount;
    }

    public int getLines() {
        return lines;
    }

    public void setLines(int lines) {
        this.lines = lines;
    }
}
