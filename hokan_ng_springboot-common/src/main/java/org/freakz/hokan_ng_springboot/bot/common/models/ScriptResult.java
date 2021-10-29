package org.freakz.hokan_ng_springboot.bot.common.models;

import java.io.Serializable;

/**
 * Created by Petri Airio on 5.4.2016.
 * -
 */
public class ScriptResult implements Serializable {

    private String scriptOutput;

    public ScriptResult() {
    }

    public ScriptResult(String scriptOutput) {
        this.scriptOutput = scriptOutput;
    }

    public String getScriptOutput() {
        return scriptOutput;
    }

    public void setScriptOutput(String scriptOutput) {
        this.scriptOutput = scriptOutput;
    }

}
