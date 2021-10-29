package org.freakz.hokan_ng_springboot.bot.common.models;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Petri Airio on 13.11.2015.
 * -
 */
public class TranslateResponse implements Serializable {


    final private String originalText;
    final private Map<String, List<TranslateData>> wordMap = new HashMap<>();

    public TranslateResponse(String originalText) {
        this.originalText = originalText;
    }

    public String getOriginalText() {
        return originalText;
    }

    public Map<String, List<TranslateData>> getWordMap() {
        return wordMap;
    }

}
