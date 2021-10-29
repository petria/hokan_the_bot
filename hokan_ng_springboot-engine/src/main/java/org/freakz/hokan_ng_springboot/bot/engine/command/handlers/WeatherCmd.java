package org.freakz.hokan_ng_springboot.bot.engine.command.handlers;

import com.martiansoftware.jsap.*;
import org.freakz.hokan_ng_springboot.bot.common.events.EngineResponse;
import org.freakz.hokan_ng_springboot.bot.common.events.InternalRequest;
import org.freakz.hokan_ng_springboot.bot.common.events.ServiceRequestType;
import org.freakz.hokan_ng_springboot.bot.common.events.ServiceResponse;
import org.freakz.hokan_ng_springboot.bot.common.exception.HokanException;
import org.freakz.hokan_ng_springboot.bot.common.models.KelikameratWeatherData;
import org.freakz.hokan_ng_springboot.bot.common.util.StringStuff;
import org.freakz.hokan_ng_springboot.bot.engine.command.HelpGroup;
import org.freakz.hokan_ng_springboot.bot.engine.command.annotation.HelpGroups;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

import static org.freakz.hokan_ng_springboot.bot.common.util.StaticStrings.*;

/**
 * User: petria
 * Date: 11/18/13
 * Time: 9:07 PM
 *
 * @author Petri Airio <petri.j.airio@gmail.com>
 */
@Component
@Scope("prototype")
@HelpGroups(
        helpGroups = {HelpGroup.DATA_FETCHERS}
)
public class WeatherCmd extends Cmd {

    public WeatherCmd() {

        setHelp("Queries weather from http://alk.tiehallinto.fi/alk/tiesaa/");

        FlaggedOption flg = new FlaggedOption(ARG_COUNT)
                .setStringParser(JSAP.INTEGER_PARSER)
                .setDefault("5")
                .setShortFlag('c');
        registerParameter(flg);

        Switch verbose = new Switch(ARG_VERBOSE)
                .setShortFlag('v');
        registerParameter(verbose);

        UnflaggedOption opt = new UnflaggedOption(ARG_PLACE)
                .setDefault("Oulu")
                .setRequired(true)
                .setGreedy(false);
        registerParameter(opt);

    }

    private String formatWeather(KelikameratWeatherData d, boolean verbose) {
        String template = "%s: %2.1f°C";
        String placeFromUrl = d.getPlaceFromUrl();
        placeFromUrl = placeFromUrl.substring(placeFromUrl.indexOf("_") + 1).replaceAll("_", " ");
        String ret = String.format(template, placeFromUrl, d.getAir(), d.getRoad(), d.getGround());
        if (verbose) {
            ret += " [" + d.getUrl() + "]";
        }
        return ret;
    }


    @Override
    public void handleRequest(InternalRequest request, EngineResponse response, JSAPResult results) throws HokanException {

        String place = results.getString(ARG_PLACE).toLowerCase();
        boolean verbose = results.getBoolean(ARG_VERBOSE);

        ServiceResponse serviceResponse = doServicesRequest(ServiceRequestType.WEATHER_REQUEST, request.getIrcEvent(), ".*");
        List<KelikameratWeatherData> data = serviceResponse.getWeatherResponse();
        if (data.size() == 0) {
            response.setResponseMessage("Weather data not ready yet!");
            return;
        }

        StringBuilder sb = new StringBuilder();

        if (place.equals("minmax")) {

            KelikameratWeatherData min = data.get(0);
            KelikameratWeatherData max = data.get(data.size() - 1);

            sb.append("Min: ");
            sb.append(formatWeather(min, verbose));
            sb.append(" Max: ");
            sb.append(formatWeather(max, verbose));
        } else if (place.equals("avg")) {

            float sum = 0.0f;
            int count = 0;
            for (KelikameratWeatherData node : data) {
                sum = sum + node.getAir();
                count++;
            }
            float avg = sum / count;
            sb.append(String.format("%d weather stations, average temperature: %2.1f°C", count, avg));

        } else {

            int xx = 0;
            String regexp = ".*" + place + ".*";
            for (KelikameratWeatherData wd : data) {
                String placeFromUrl = wd.getPlaceFromUrl();
                String stationFromUrl = wd.getUrl().getStationUrl();
                if (StringStuff.match(placeFromUrl, regexp) || StringStuff.match(stationFromUrl, regexp)) {
                    if (wd.getAir() == null) {
                        continue;
                    }
                    if (xx != 0) {
                        sb.append(", ");
                    }
                    sb.append(formatWeather(wd, verbose));
                    xx++;
                    if (xx > results.getInt(ARG_COUNT)) {
                        break;
                    }
                }
            }
            if (xx == 0) {
                String hhmmss = StringStuff.formatTime(new Date(), StringStuff.STRING_STUFF_DF_HHMMSS);
                sb.append(String.format("%s %s 26.7°C, hellettä pukkaa!", hhmmss, place));
            }
        }

        response.setResponseMessage(sb.toString());
    }
}
