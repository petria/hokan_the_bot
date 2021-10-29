package org.freakz.hokan_ng_springboot.bot.engine.command.handlers;

import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.UnflaggedOption;
import org.freakz.hokan_ng_springboot.bot.common.events.EngineResponse;
import org.freakz.hokan_ng_springboot.bot.common.events.InternalRequest;
import org.freakz.hokan_ng_springboot.bot.common.events.ServiceRequestType;
import org.freakz.hokan_ng_springboot.bot.common.events.ServiceResponse;
import org.freakz.hokan_ng_springboot.bot.common.exception.HokanException;
import org.freakz.hokan_ng_springboot.bot.common.models.CityData;
import org.freakz.hokan_ng_springboot.bot.common.models.HourlyWeatherData;
import org.freakz.hokan_ng_springboot.bot.common.service.cityresolver.CityResolver;
import org.freakz.hokan_ng_springboot.bot.engine.command.HelpGroup;
import org.freakz.hokan_ng_springboot.bot.engine.command.annotation.HelpGroups;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static org.freakz.hokan_ng_springboot.bot.common.util.StaticStrings.ARG_PLACE;

/**
 * Created by Petri Airio on 11.8.2016.
 */
@Component
@Scope("prototype")
@HelpGroups(
        helpGroups = {HelpGroup.DATA_FETCHERS}
)
public class WeatherHourlyCmd extends Cmd {

    @Autowired
    private CityResolver cityResolver;

    public WeatherHourlyCmd() {

        UnflaggedOption opt = new UnflaggedOption(ARG_PLACE)
                .setDefault("Oulu")
                .setRequired(true)
                .setGreedy(true);
        registerParameter(opt);
        setHelp("Queries weather from https://www.ilmatieteenlaitos.fi/saa/");
    }

    @Override
    public void handleRequest(InternalRequest request, EngineResponse response, JSAPResult results) throws HokanException {
        String place = results.getString(ARG_PLACE).toLowerCase();
        String[] args = results.getStringArray(ARG_PLACE);
        boolean matchOnlyOne = false;
        if (args.length == 1) {
            if (place.startsWith("=")) {
                place = place.substring(1);
                args[0] = place;
                matchOnlyOne = true;
            }
        }

        CityData cityData = cityResolver.resolveCityNames(args);

        List<String> hoursL = new ArrayList<>();
        List<String> tempsL = new ArrayList<>();

        List<HourlyWeatherData> datas = new ArrayList<>();
        for (String city : cityData.getResolvedCityNames()) {
            if (matchOnlyOne) {
                if (!city.equalsIgnoreCase(place)) {
                    continue;
                }
            }

            ServiceResponse serviceResponse = doServicesRequest(ServiceRequestType.ILMATIETEENLAITOS_HOURLY_REQUEST, request.getIrcEvent(), city);
            HourlyWeatherData hourlyWeatherData = serviceResponse.getHourlyWeatherData();
            if (hourlyWeatherData != null && hourlyWeatherData.getTimes() != null) {
                datas.add(hourlyWeatherData);
            }
        }

        if (datas.size() == 0) {
            response.addResponse("Nothing found: %s!!", place);
        } else {
            if (datas.size() == 1) {
                String city = cityData.getResolvedCityNames().get(0);
                HourlyWeatherData hourlyWeatherData = datas.get(0);
                StringBuilder sb = new StringBuilder();
                sb.append("Hourly forecast: ");
                sb.append(city);
                sb.append("\n");
                String hours = "";
                String temps = "";
                String format = getFormat(hourlyWeatherData.getLongestTemp());
                for (int i = 0; i < hourlyWeatherData.getTimes().length; i++) {
                    hours += String.format(format, hourlyWeatherData.getTimes()[i]);
                    temps += String.format(format, hourlyWeatherData.getTemperatures()[i]);
                }
                hoursL.add(hours);
                tempsL.add(temps);

                sb.append(hours);
                sb.append("\n");
                sb.append(temps);
                sb.append("\n");
                response.addResponse("%s", sb.toString());

            } else {
                String cities = "";
                String tempss = "";
                String hourss = null;
                for (int i = 0; i < datas.size(); i++) {
                    if (i > 0) {
                        cities += ", ";
                    }
                    cities += cityData.getResolvedCityNames().get(i);

                    HourlyWeatherData hourlyWeatherData = datas.get(i);
                    String hours = "";
                    String temps = "";

                    String format = getFormat(hourlyWeatherData.getLongestTemp());
                    for (int i2 = 0; i2 < hourlyWeatherData.getTimes().length; i2++) {
                        hours += String.format(format, hourlyWeatherData.getTimes()[i2]);
                        temps += String.format(format, hourlyWeatherData.getTemperatures()[i2]);
                    }
                    if (hourss == null) {
                        hourss = hours;
                    }
                    tempss += temps + "\n";

                }
                String resp = "Hourly forecast: " + cities + "\n";
                resp += hourss + "\n";
                resp += tempss + "\n";

                response.addResponse(resp);
            }
        }


/*
      for (String city : cityData.getResolvedCityNames()) {

      ServiceResponse serviceResponse = doServicesRequest(ServiceRequestType.ILMATIETEENLAITOS_HOURLY_REQUEST, request.getIrcEvent(), city);
      HourlyWeatherData hourlyWeatherData = serviceResponse.getHourlyWeatherData();


      if (hourlyWeatherData != null && hourlyWeatherData.getTimes() != null) {
        StringBuilder sb = new StringBuilder();
        sb.append("Hourly forecast: ");
        sb.append(city);
        sb.append("\n");
        String hours = "";
        String temps = "";
        String format = getFormat(hourlyWeatherData.getLongestTemp());
        for (int i = 0; i < hourlyWeatherData.getTimes().length; i++) {
          hours += String.format(format, hourlyWeatherData.getTimes()[i]);
          temps += String.format(format, hourlyWeatherData.getTemperatures()[i]);
        }
        hoursL.add(hours);
        tempsL.add(temps);

        sb.append(hours);
        sb.append("\n");
        sb.append(temps);
        sb.append("\n");
        response.addResponse("%s", sb.toString());

      } else {

        response.addResponse("Nothing found: %s!!", place);

      }
    }
    for (int i = 0; i < tempsL.size(); i++) {

    }
*/
    }

    private String getFormat(int longestTemp) {
        String format = "%" + (longestTemp + 1) + "s";
        return format;
    }


}
