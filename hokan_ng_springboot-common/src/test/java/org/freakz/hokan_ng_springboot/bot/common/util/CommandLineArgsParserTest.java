package org.freakz.hokan_ng_springboot.bot.common.util;


import org.freakz.hokan_ng_springboot.bot.common.enums.CommandLineArgs;
import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

/**
 * Created by Petri Airio on 13.11.2015.
 * -
 */
public class CommandLineArgsParserTest {

    @Test
    public void testParseCanHandleNull() {
        String[] args = null;
        CommandLineArgsParser parser = new CommandLineArgsParser(args);
        Map<CommandLineArgs, String> parsed = parser.parseArgs();

        Assert.assertEquals(0, parsed.size());
    }


    @Test
    public void testParseArgsNonValid() {
        String[] args = {"foobar"};
        CommandLineArgsParser parser = new CommandLineArgsParser(args);
        Map<CommandLineArgs, String> parsed = parser.parseArgs();

        Assert.assertEquals(0, parsed.size());
    }

    @Test
    public void testParseValidArgNoValue() {
        String[] args = {"--JmsBrokerUrl="};
        CommandLineArgsParser parser = new CommandLineArgsParser(args);
        Map<CommandLineArgs, String> parsed = parser.parseArgs();

        Assert.assertEquals(0, parsed.size());
    }

    @Test
    public void testParseValidArgValue() {
        String[] args = {"--JmsBrokerUrl=value"};
        CommandLineArgsParser parser = new CommandLineArgsParser(args);
        Map<CommandLineArgs, String> parsed = parser.parseArgs();

        Assert.assertEquals(1, parsed.size());
        Assert.assertEquals("value", parsed.get(CommandLineArgs.JMS_BROKER_URL));
    }

    @Test
    public void testParseArgWithoutValue() {
        String[] args = {"--ConfigInit"};
        CommandLineArgsParser parser = new CommandLineArgsParser(args);
        Map<CommandLineArgs, String> parsed = parser.parseArgs();
        Assert.assertEquals(1, parsed.size());
    }

}
