package org.freakz.hokan_ng_springboot.bot.services.service.annotation;

import org.freakz.hokan_ng_springboot.bot.common.enums.LunchPlace;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Petri Airio on 21.1.2016.
 * -
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface LunchPlaceHandler {

    LunchPlace LunchPlace();

}
