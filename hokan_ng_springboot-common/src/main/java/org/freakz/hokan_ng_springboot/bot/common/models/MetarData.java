package org.freakz.hokan_ng_springboot.bot.common.models;

import java.io.Serializable;

/**
 * User: petria
 * Date: 11/26/13
 * Time: 1:09 PM
 *
 * @author Petri Airio <petri.j.airio@gmail.com>
 */
public class MetarData implements Serializable {

    private String metarData;

    public MetarData(String metarData) {
        this.metarData = metarData;
    }

    public String getMetarData() {
        return metarData;
    }

    public void setMetarData(String metarData) {
        this.metarData = metarData;
    }
}
