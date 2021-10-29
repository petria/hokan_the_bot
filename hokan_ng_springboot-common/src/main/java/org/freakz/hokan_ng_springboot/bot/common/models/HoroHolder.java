package org.freakz.hokan_ng_springboot.bot.common.models;

import java.io.Serializable;

/**
 * User: petria
 * Date: Jul 15, 2009
 * Time: 8:29:17 AM
 */
public class HoroHolder implements Serializable {

    private String horoscopeText;

    public HoroHolder(String horoscopeText) {
        this.horoscopeText = horoscopeText;
    }

    public String getHoroscopeText() {
        return horoscopeText;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
/*        sb.append(HORO_NAMES[getHoroscope()]);
        sb.append(" (");
        sb.append(HORO_DATES[getHoroscope()]);
        sb.append("): ");*/
        sb.append(this.horoscopeText);
        return sb.toString();
    }

}
