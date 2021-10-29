package org.freakz.hokan_ng_springboot.bot.engine.command.annotation;

import org.freakz.hokan_ng_springboot.bot.engine.command.HelpGroup;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Petri Airio on 15.9.2015.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface HelpGroups {

    HelpGroup[] helpGroups();

}
