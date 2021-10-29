package org.freakz.hokan_ng_springboot.bot.engine;

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
import java.util.Map;

@Configuration
@EnableAutoConfiguration
@EnableJms
@ComponentScan({"org.freakz.hokan_ng_springboot.bot"})
public class HokanNgSpringBootEngine {

    private static final Logger log = LoggerFactory.getLogger(HokanNgSpringBootEngine.class);

    private static String JMS_BROKER_URL = "tcp://localhost:61616";

    @Bean
    public ConnectionFactory connectionFactory() {
        ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory(JMS_BROKER_URL);
        activeMQConnectionFactory.setTrustAllPackages(true);
        return activeMQConnectionFactory;
    }

    public static void main(String[] args) {
        CommandLineArgsParser parser = new CommandLineArgsParser(args);
        Map<CommandLineArgs, String> parsed = parser.parseArgs();
        String url = parsed.get(CommandLineArgs.JMS_BROKER_URL);
        if (url != null) {
            JMS_BROKER_URL = url;
        }
        log.debug("JMS_BROKER_URL: {}", JMS_BROKER_URL);
        SpringApplication.run(HokanNgSpringBootEngine.class, args);
    }

}
