package org.freakz.hokan_ng_springboot.bot.common.events;

import org.freakz.hokan_ng_springboot.bot.common.enums.LunchPlace;
import org.freakz.hokan_ng_springboot.bot.common.models.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Petri Airio (petri.j.airio@gmail.com) on 24.4.2015.
 * -
 */
@SuppressWarnings("unchecked")
public class ServiceResponse implements Serializable {

    private final ServiceRequestType type;
    private final Map<String, Object> responseData = new HashMap<>();

    public ServiceResponse(ServiceRequestType type) {
        this.type = type;
    }

    public void setResponseData(String key, Object data) {
        responseData.put(key, data);
    }

    public Object getResponseData(String key) {
        return responseData.get(key);
    }

    public List<MetarData> getMetarResponse() {
        List<MetarData> dataList = (List<MetarData>) responseData.get(type.getResponseDataKey());
        if (dataList == null) {
            return new ArrayList<>();
        }
        return dataList;
    }

    public String getHoroResponse() {
        return (String) responseData.get(type.getResponseDataKey());
    }

    public List<KelikameratWeatherData> getWeatherResponse() {
        List<KelikameratWeatherData> data = (List<KelikameratWeatherData>) responseData.get(type.getResponseDataKey());
        if (data == null) {
            return new ArrayList<>();
        }
        return data;
    }

    public List<DataUpdaterModel> getUpdaterListData() {
        List<DataUpdaterModel> data = (List<DataUpdaterModel>) responseData.get(type.getResponseDataKey());
        if (data == null) {
            return new ArrayList<>();
        }
        return data;
    }

    public List<DataUpdaterModel> getStartUpdaterListData() {
        List<DataUpdaterModel> data = (List<DataUpdaterModel>) responseData.get(type.getResponseDataKey());
        if (data == null) {
            return new ArrayList<>();
        }
        return data;
    }

    public String getCurrencyConvertResponse() {
        return (String) responseData.get(type.getResponseDataKey());
    }

    public List<GoogleCurrency> getCurrencyListResponse() {
        return (List<GoogleCurrency>) responseData.get(type.getResponseDataKey());
    }

    public NimipaivaData getNimipaivaDayResponse() {
        return (NimipaivaData) responseData.get(type.getResponseDataKey());

    }

    public NimipaivaData getNimipaivaNameResponse() {
        return (NimipaivaData) responseData.get(type.getResponseDataKey());
    }

    public TranslateResponse getTranslateResponse() {
        return (TranslateResponse) responseData.get(type.getResponseDataKey());
    }

    public IMDBSearchResults getIMDBTitleData() {
        return (IMDBSearchResults) responseData.get(type.getResponseDataKey());
    }

    public IMDBDetails getIMDBDetails() {
        return (IMDBDetails) responseData.get(type.getResponseDataKey());
    }

    public List<LunchPlace> getLunchPlacesResponse() {
        return (List<LunchPlace>) responseData.get(type.getResponseDataKey());
    }

    public LunchPlaceData getLunchResponse() {
        return (LunchPlaceData) responseData.get(type.getResponseDataKey());
    }

    public String[] getCharsetResponse() {
        return (String[]) responseData.get(type.getResponseDataKey());
    }

    public ScriptResult getScriptResult() {
        return (ScriptResult) responseData.get(type.getResponseDataKey());
    }

    public String getSunRiseResponse() {
        return (String) responseData.get(type.getResponseDataKey());
    }

    public HourlyWeatherData getHourlyWeatherData() {
        return (HourlyWeatherData) responseData.get(type.getResponseDataKey());
    }

    public ChannelSetTopic getChannelSetTopic() {
        return (ChannelSetTopic) responseData.get(type.getResponseDataKey());
    }

    public AlkoSearchResults getAlkoSearchResults() {
        return (AlkoSearchResults) responseData.get(type.getResponseDataKey());
    }

    public FindCityResults getFindCityResults() {
        return (FindCityResults) responseData.get(type.getResponseDataKey());
    }

    public SendSMSResponse getSendSMSResponse() {
        return (SendSMSResponse) responseData.get(type.getResponseDataKey());
    }

    public String getSendSMSCreditResponse() {
        return (String) responseData.get(type.getResponseDataKey());
    }

    public Integer[] getKoronas() {
        return (Integer[]) responseData.get(type.getResponseDataKey());
    }

}
