package org.freakz.hokan_ng_springboot.bot.services;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.jms.annotation.EnableJms;

@SpringBootApplication
@EnableJms
@ComponentScan({"org.freakz.hokan_ng_springboot.bot"})
public class HokanNgSpringbootServicesApplication {

    public static void main(String[] args) {
        SpringApplication.run(HokanNgSpringbootServicesApplication.class, args);
    }

}
