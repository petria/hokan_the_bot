package org.freakz.hokan_ng_springboot.bot.services.service.patch;

import lombok.extern.slf4j.Slf4j;
import org.freakz.hokan_ng_springboot.bot.common.events.IrcMessageEvent;
import org.freakz.hokan_ng_springboot.bot.common.events.ServiceRequest;
import org.freakz.hokan_ng_springboot.bot.common.events.ServiceResponse;
import org.freakz.hokan_ng_springboot.bot.services.service.topcounter.TopCountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.freakz.hokan_ng_springboot.bot.common.events.ServiceRequestType.TOP_COUNT_REQUEST;

@Service
@Slf4j
public class LogFileFeedService implements CommandLineRunner {

    private static String[] LOG_DIRS = {
            "/Users/petria/code/git/hokan_ng_springboot-services/logs/ircnet/#amigafin"
    };


    private final TopCountService topCountService;

    @Autowired
    public LogFileFeedService(TopCountService topCountService) {

        this.topCountService = topCountService;
    }

    public void feedLogs() throws IOException {

        for (String logDir : LOG_DIRS) {
            File f = new File(logDir);

            String[] files = f.list();

            processLogFiles(logDir, files);

        }

    }

    private void processLogFiles(String logDir, String[] files) throws IOException {

        int totalLines = 0;
        int totalFiles = 0;
        for (String logFile : files) {

            LocalDateTime logFileDate = parseLocalDateTimeFromFileName(logFile);
            var ref = new Object() {
                int lineCount = 0;
            };
//            log.debug("Start processing of file: {}", logFile);
            Files.lines(Path.of(logDir + "/" + logFile)).forEach(line -> {
                handleLogLine(logDir, logFileDate, line);
                ref.lineCount++;
            });
            log.debug("file: {} done, lines: {}", logFile, ref.lineCount);
            totalFiles++;
            totalLines += ref.lineCount;
        }
        log.debug("totalFiles: {} done, totalLines: {}", totalFiles, totalLines);

    }

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private LocalDateTime parseLocalDateTimeFromFileName(String logFile) {
        String datePart = logFile.substring(0, 10) + " 00:00";
        LocalDateTime date = LocalDateTime.parse(datePart, formatter);
        return date;
    }

    private void handleLogLine(String logDir, LocalDateTime logFileDate, String line) {
        LocalDateTime logLineDate = setTimeFromLogLine(logFileDate, line);

        ServiceRequest request = createServiceRequest(line, logLineDate, logDir);
        ServiceResponse response = new ServiceResponse(request.getType());

        topCountService.calculateTopCounters(request, response);
        int foo = 0;

    }

    private LocalDateTime setTimeFromLogLine(LocalDateTime logFileDate, String line) {

        String wordWithTime = line.split(" ")[0];
        String[] times = wordWithTime.split(":");
        int ss = 0;
        if (times.length != 2 && times.length != 3) {
            int foo = 0;
        }
        if (times.length == 3) {
            ss = Integer.parseInt(times[2]);
        }

        LocalDateTime lineDate = logFileDate.withHour(Integer.parseInt(times[0])).withMinute(Integer.parseInt(times[1])).withSecond(ss);

        return lineDate;
    }

    private ServiceRequest createServiceRequest(String line, LocalDateTime lineDate, String logDir) {

        String[] split = logDir.split("/");
        String network = split[split.length - 2];
        String channel = split[split.length - 1];

        int idx = line.indexOf(": ");
        String sender = line.substring(9, idx);
        String message = line.substring(idx + 2);

        IrcMessageEvent event = new IrcMessageEvent();
        event.setNetwork(network);
        event.setChannel(channel);
        event.setTimeStamp(lineDate);
        event.setMessage(message);
        event.setSender(sender);

        ServiceRequest request = new ServiceRequest(TOP_COUNT_REQUEST, event, null, null);
        return request;
    }


    @Override
    public void run(String... args) throws Exception {
//        feedLogs();
    }
}
