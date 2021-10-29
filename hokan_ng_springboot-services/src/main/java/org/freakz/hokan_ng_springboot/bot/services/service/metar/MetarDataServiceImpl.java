package org.freakz.hokan_ng_springboot.bot.services.service.metar;

import org.freakz.hokan_ng_springboot.bot.common.models.MetarData;
import org.freakz.hokan_ng_springboot.bot.common.util.FileUtil;
import org.freakz.hokan_ng_springboot.bot.common.util.StringStuff;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: petria
 * Date: 11/26/13
 * Time: 1:06 PM
 *
 * @author Petri Airio <petri.j.airio@gmail.com>
 */
@Service
public class MetarDataServiceImpl implements MetarDataService {

    private static final Logger log = LoggerFactory.getLogger(MetarDataServiceImpl.class);

    @Autowired
    private FileUtil fileUtil;

    private String[] stations = null;

    public MetarDataServiceImpl() {
    }

    private String[] fetchMetarData(String stationID) {

        StringBuilder buffer = new StringBuilder();
        try {

            String urlStr = "ftp://tgftp.nws.noaa.gov/data/observations/metar/decoded/" + stationID + ".TXT";
            URL url = new URL(urlStr);
            URLConnection conn = url.openConnection();
            InputStream in = conn.getInputStream();
            InputStreamReader isr = new InputStreamReader(in);
            BufferedReader br =
                    new BufferedReader(isr);
            String l;
            do {
                l = br.readLine();
                if (l != null) {
                    buffer.append(l);
                    buffer.append("\n");
                }
            } while (l != null);

        } catch (Exception e) {
            //
        }
        return buffer.toString().split("\n");
    }

    private List<String> getStations(String pattern) {
        List<String> matches = new ArrayList<>();
        if (stations == null) {
            log.info("Reading stations file");
            StringBuilder sb = new StringBuilder();
            File tmpFile;
            try {
                tmpFile = File.createTempFile("stations", "");
                fileUtil.copyResourceToFile("/nsd_bbsss.txt", tmpFile, sb);
                stations = sb.toString().split("\n");
            } catch (IOException e) {
                log.error("Failed to get metar stations", e);
                return matches;
            }
        }
        for (String station : stations) {
            if (StringStuff.match(station, ".*" + pattern + ".*", true)) {
                matches.add(station);
            }
        }
        return matches;
    }


    @Override
    public List<MetarData> getMetarData(Object... parameters) {
        String station = (String) parameters[0]; // TODO
        List<MetarData> metarDatas = new ArrayList<>();
        List<String> metars = getStations(station);
        if (metars.size() > 0) {
            for (String metarName : metars) {
                String[] split = metarName.split(";");
                if (split[2].equals("----")) {
                    log.info("Skipping: {}", metarName);
                    continue;
                }
                String ret = "";
                String[] metarData = fetchMetarData(split[2]);
                String temp = "??";
                String date = "";
                String conds = "";
                for (String row : metarData) {
                    if (row.endsWith("UTC")) {
                        Pattern p = Pattern.compile(".* / (\\d\\d\\d\\d\\.\\d\\d\\.\\d\\d \\d\\d\\d\\d) UTC");
                        Matcher m = p.matcher(row);
                        if (m.find()) {
                            String dateStr = m.group(1);
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HHmm Z");
                            try {
                                dateStr += " +0000";
                                Date d = sdf.parse(dateStr);
                                date = StringStuff.formatNiceDate(d, false) + ", ";
                            } catch (java.text.ParseException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    if (row.startsWith("Temperature: ")) {
                        temp = row.replaceAll("Temperature: ", "");
                    }
                    if (row.startsWith("Sky conditions: ")) {
                        conds = ", " + row.replaceAll("Sky conditions: ", "");
                    }
                }
                ret += split[2] + " " + split[3] + " " + split[4] + "= " + date + temp + conds;
                metarDatas.add(new MetarData(ret));
            }
        }
        return metarDatas;

    }
}
