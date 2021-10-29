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

import static org.freakz.hokan_ng_springboot.bot.common.util.StaticStrings.ARG_PLACE;

/**
 * Created by airiope on 5.5.2017.
 */
@Component
@Scope("prototype")
@HelpGroups(
        helpGroups = {HelpGroup.DATA_FETCHERS}
)
public class WeatherTerassiCmd extends Cmd {

    @Autowired
    private CityResolver cityResolver;

    public WeatherTerassiCmd() {

        UnflaggedOption opt = new UnflaggedOption(ARG_PLACE)
                .setDefault("Oulu")
                .setRequired(true)
                .setGreedy(false);
        registerParameter(opt);

        setHelp("Shows optimal time to go to the TERASSI.");

    }

    private MinMax getOptimalHours(HourlyWeatherData hourlyWeatherData) {
        int[] tempTable = new int[hourlyWeatherData.getTemperatures().length];
        int idx = 0;
        int maxTemp = Integer.MIN_VALUE;
        int maxTempIdx = -1;
        for (String str : hourlyWeatherData.getTemperatures()) {
            int t = Integer.parseInt(str.replaceAll("°", ""));
            tempTable[idx] = t;
            if (t > maxTemp) {
                maxTemp = t;
                maxTempIdx = idx;
            }
            idx++;
        }
        int minIdx;
        for (minIdx = maxTempIdx; minIdx > 0; minIdx--) {
            if (tempTable[minIdx] != maxTemp) {
                break;
            }
        }
        int maxIdx;
        for (maxIdx = maxTempIdx; maxIdx < tempTable.length; maxIdx++) {
            if (tempTable[maxIdx] != maxTemp) {
                break;
            }
        }
        MinMax mm = new MinMax();
        mm.minIdx = minIdx + 1;
        mm.maxIdx = maxIdx - 1;
        return mm;
    }

    @Override
    public void handleRequest(InternalRequest request, EngineResponse response, JSAPResult results) throws HokanException {
        String place = results.getString(ARG_PLACE).toLowerCase();
        CityData cityData = cityResolver.resolveCityNames(place);

        for (String city : cityData.getResolvedCityNames()) {

            ServiceResponse serviceResponse = doServicesRequest(ServiceRequestType.ILMATIETEENLAITOS_HOURLY_REQUEST, request.getIrcEvent(), city);
            HourlyWeatherData hourlyWeatherData = serviceResponse.getHourlyWeatherData();
            if (hourlyWeatherData.getTimes() != null) {
                MinMax mm = getOptimalHours(hourlyWeatherData);
                StringBuilder sb = new StringBuilder();
                sb.append(city);
                sb.append(" terassi time: ");
                sb.append(hourlyWeatherData.getTimes()[mm.minIdx]);
                sb.append("-");
                sb.append(hourlyWeatherData.getTimes()[mm.maxIdx]);
                sb.append(" ");
                sb.append(hourlyWeatherData.getTemperatures()[mm.minIdx]);
                sb.append("\n");

                response.addResponse("%s", sb.toString());

            }

        }
    }

    static class MinMax {
        int minIdx;
        int maxIdx;
    }
}
