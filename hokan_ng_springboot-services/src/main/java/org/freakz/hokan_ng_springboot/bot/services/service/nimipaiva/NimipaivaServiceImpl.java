package org.freakz.hokan_ng_springboot.bot.services.service.nimipaiva;

/**
 * Created by Petri Airio on 5.10.2015.
 * -
 */

import org.freakz.hokan_ng_springboot.bot.common.models.NimipaivaData;
import org.freakz.hokan_ng_springboot.bot.common.util.FileUtil;
import org.freakz.hokan_ng_springboot.bot.common.util.StringStuff;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Service
public class NimipaivaServiceImpl implements NimipaivaService {

    private static final Logger log = LoggerFactory.getLogger(NimipaivaServiceImpl.class);

    private static final String NIMIPAIVAT_TXT = "/Nimipaivat.txt";

    private Map<LocalDateTime, NimipaivaData> dateTimeNamesMap = new HashMap<>();

    @PostConstruct
    public void loadNames() {
        FileUtil fileUtil = new FileUtil();
        StringBuilder contents = new StringBuilder();
        try {
            fileUtil.copyResourceToTmpFile(NIMIPAIVAT_TXT, contents);
            String[] rows = contents.toString().split("\n");
            for (String row : rows) {
                String[] split1 = row.split("\\. ");
                if (split1.length == 2) {
                    String[] date = split1[0].split("\\.");

//                        DateTime dateTime = DateTime.now().withDayOfMonth(Integer.parseInt(date[0])).withMonthOfYear(Integer.parseInt(date[1]));
                    try {
                        int day = Integer.parseInt(date[0]);
                        int month = Integer.parseInt(date[1]);
                        LocalDateTime localDateTime = LocalDateTime.now().withMonth(month);
                        localDateTime = localDateTime.withDayOfMonth(day);
                        String[] names = split1[1].split(", ");
                        NimipaivaData nimipaivaData = new NimipaivaData(localDateTime, Arrays.asList(names));
                        dateTimeNamesMap.put(localDateTime, nimipaivaData);

                    } catch (DateTimeException ex) {
                        log.error(ex.getMessage() + " :: " + row);
                    }
                }
            }
        } catch (IOException e) {
            log.error("Can't load {}", NIMIPAIVAT_TXT);
        }
    }

    private NimipaivaData findByDay(LocalDateTime day) {
        int dayOfMont = day.getDayOfMonth();
        int monthOfYear = day.getMonthValue();
        for (NimipaivaData nimipaivaData : dateTimeNamesMap.values()) {
            int dayOfMont2 = nimipaivaData.getDay().getDayOfMonth();
            int monthOfYear2 = nimipaivaData.getDay().getMonthValue();
            if (dayOfMont == dayOfMont2 && monthOfYear == monthOfYear2) {
                return nimipaivaData;
            }
        }
        return null;
    }

    private NimipaivaData findByName(String nameToFind) {
        for (NimipaivaData nimipaivaData : dateTimeNamesMap.values()) {
            for (String name : nimipaivaData.getNames()) {
                if (StringStuff.match(name, nameToFind, true)) {
                    return nimipaivaData;
                }
            }
        }
        return null;
    }

    @Override
    public NimipaivaData getNamesForDay(LocalDateTime day) {
        NimipaivaData nimipaivaData = findByDay(day);
        if (nimipaivaData != null) {
            return nimipaivaData;
        }
        return null;
    }

    @Override
    public NimipaivaData findDayForName(String name) {
        NimipaivaData nimipaivaData = findByName(name);
        if (nimipaivaData != null) {
            return nimipaivaData;
        }
        return null;
    }
}
