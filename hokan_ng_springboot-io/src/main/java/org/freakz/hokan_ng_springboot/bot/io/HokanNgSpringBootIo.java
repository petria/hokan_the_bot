package org.freakz.hokan_ng_springboot.bot.io;

import ch.qos.logback.classic.Level;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.freakz.hokan_ng_springboot.bot.common.enums.CommandLineArgs;
import org.freakz.hokan_ng_springboot.bot.common.util.CommandLineArgsParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;

import javax.jms.ConnectionFactory;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

@Configuration
@EnableAutoConfiguration
@EnableJms
@ComponentScan({"org.freakz.hokan_ng_springboot.bot"})
public class HokanNgSpringBootIo {

    private static final Logger log = LoggerFactory.getLogger(HokanNgSpringBootIo.class);

    private static String JMS_BROKER_URL = "tcp://localhost:61616";

    @Bean
    public ConnectionFactory connectionFactory() {
        ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory(JMS_BROKER_URL);
        activeMQConnectionFactory.setTrustAllPackages(true);
        return activeMQConnectionFactory;
    }

    public static void main(String[] args) throws UnsupportedEncodingException, FileNotFoundException {
        CommandLineArgsParser parser = new CommandLineArgsParser(args);
        Map<CommandLineArgs, String> parsed = parser.parseArgs();
        String url = parsed.get(CommandLineArgs.JMS_BROKER_URL);
        if (url != null) {
            JMS_BROKER_URL = url;
        }
        log.debug("JMS_BROKER_URL: {}", JMS_BROKER_URL);
        if (parsed.containsKey(CommandLineArgs.CONFIG_INIT)) {
            log.debug("Disabling LogBack logger!");
            ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) org.slf4j.LoggerFactory.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
            root.setLevel(Level.OFF);
        }
        SpringApplication.run(HokanNgSpringBootIo.class, args);
    }

}
