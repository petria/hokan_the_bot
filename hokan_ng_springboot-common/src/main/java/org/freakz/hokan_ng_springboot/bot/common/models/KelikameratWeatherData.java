package org.freakz.hokan_ng_springboot.bot.common.models;


import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Created by Petri Airio on 23.6.2015.
 * -
 */
public class KelikameratWeatherData implements Comparable, Serializable {

    private LocalDateTime time;

    private KelikameratUrl url;

    private String place;
    private String placeFromUrl;

    private Float air;
    private Float road;
    private Float ground;

    private Float humidity;
    private Float dewPoint;
    private int pos;
    private int count;


    public KelikameratWeatherData() {
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public KelikameratUrl getUrl() {
        return url;
    }

    public void setUrl(KelikameratUrl url) {
        this.url = url;
    }

    public Float getAir() {
        return air;
    }

    public void setAir(Float air) {
        this.air = air;
    }

    public Float getRoad() {
        return road;
    }

    public void setRoad(Float road) {
        this.road = road;
    }

    public Float getGround() {
        return ground;
    }

    public void setGround(Float ground) {
        this.ground = ground;
    }

    public Float getHumidity() {
        return humidity;
    }

    public void setHumidity(Float humidity) {
        this.humidity = humidity;
    }

    public Float getDewPoint() {
        return dewPoint;
    }

    public void setDewPoint(Float dewPoint) {
        this.dewPoint = dewPoint;
    }

    public String getPlaceFromUrl() {
        return this.placeFromUrl;
    }

    public void setPlaceFromUrl(String placeFromUrl) {
        this.placeFromUrl = placeFromUrl;
    }

    @Override
    public int compareTo(Object other) {
        KelikameratWeatherData w1 = this;
        KelikameratWeatherData w2 = (KelikameratWeatherData) other;
        Float air1 = w1.getAir();
        Float air2 = w2.getAir();
        return air1.compareTo(air2);
    }

    public int getPos() {
        return pos;
    }

    public void setPos(int i) {
        this.pos = i;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int size) {
        this.count = size;
    }
}
