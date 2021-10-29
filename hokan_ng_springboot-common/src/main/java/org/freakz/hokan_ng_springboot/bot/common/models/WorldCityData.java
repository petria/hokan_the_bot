package org.freakz.hokan_ng_springboot.bot.common.models;

import java.io.Serializable;

public class WorldCityData implements Serializable {

    private String countryCode;

    private String city;

    private String accentCity;

    private String region;

    private Double latitude;

    private Double longitude;

    public String getCountryCode() {
        return countryCode;
    }

    @Override
    public String toString() {
        return String.format("%s %s %s %s", countryCode, city, accentCity, region);
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAccentCity() {
        return accentCity;
    }

    public void setAccentCity(String accentCity) {
        this.accentCity = accentCity;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
}
