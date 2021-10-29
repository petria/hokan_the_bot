package org.freakz.hokan_ng_springboot.bot.io.service.configinit;

import org.freakz.hokan_ng_springboot.bot.common.jpa.entity.*;
import org.freakz.hokan_ng_springboot.bot.common.jpa.service.ChannelService;
import org.freakz.hokan_ng_springboot.bot.common.jpa.service.IrcServerConfigService;
import org.freakz.hokan_ng_springboot.bot.common.jpa.service.NetworkService;
import org.freakz.hokan_ng_springboot.bot.common.jpa.service.PropertyService;
import org.freakz.hokan_ng_springboot.bot.common.util.StringStuff;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Created by Petri Airio on 7.3.2017.
 */
@Component
public class ConfigurationInitImpl implements ConfigurationInit {

    private static Scanner sc = new Scanner(System.in);

    @Autowired
    private ChannelService channelService;

    @Autowired
    private IrcServerConfigService ircServerConfigService;

    @Autowired
    private NetworkService networkService;

    @Autowired
    private PropertyService propertyService;

    @Override
    public void initConfiguration() {
        Map<ConfigurationItems, String> answers = configureItems();

        System.out.printf("-- Creating database items --\n");
        boolean ok = createDatabaseItems(answers);
        System.out.printf("-- create returned: %s\n", ok);


    }

    private boolean createDatabaseItems(Map<ConfigurationItems, String> answers) {
        String botName = answers.get(ConfigurationItems.BOT_NAME);
        PropertyEntity propertyEntity = new PropertyEntity(PropertyName.PROP_SYS_BOT_NICK, botName, "");

        System.out.printf("Setting bot name ...\n");
        propertyEntity = propertyService.save(propertyEntity);
        System.out.printf("Bot name set to: %s\n\n", propertyEntity.getValue());

        String adminUserToken = StringStuff.generatePasswd(10);
        PropertyEntity masterUserTokenPropertyEntity = new PropertyEntity(PropertyName.PROP_SYS_ADMIN_USER_TOKEN, adminUserToken, "");
        masterUserTokenPropertyEntity = propertyService.save(masterUserTokenPropertyEntity);

        System.out.printf("***************************************************\n");
        System.out.printf("*           !!!!!!  IMPORTANT !!!!!!              *\n");
        System.out.printf("*                                                 *\n");

        System.out.printf("  ADMIN USER TOKEN IS: %s\n", adminUserToken);
        System.out.printf("  DO: /msg %s @AdminUserToken %s\n", botName, adminUserToken);
        System.out.printf("  TO GET ADMIN RIGHTS\n");

        System.out.printf("*                                                 *\n");
        System.out.printf("*           !!!!!!  IMPORTANT !!!!!!              *\n");
        System.out.printf("***************************************************\n\n");

        System.out.printf("Creating Network ...\n");
        Network network = new Network(answers.get(ConfigurationItems.NETWORK_NAME));
        network = networkService.save(network);
        System.out.printf("Network created: %s\n\n", network.toString());

        System.out.printf("Creating IrcServerConfig for Network ...\n");
        IrcServerConfig ircServerConfig = new IrcServerConfig();
        ircServerConfig.setNetwork(network);
        ircServerConfig.setServer(answers.get(ConfigurationItems.IRC_SERVER_ADDRESS));
        ircServerConfig.setPort(Integer.parseInt(answers.get(ConfigurationItems.IRC_SERVER_PORT)));
        ircServerConfig.setServerPassword(answers.get(ConfigurationItems.IRC_SERVER_PASSWORD));
        ircServerConfig.setUseThrottle(1);
        ircServerConfig.setIrcServerConfigState(IrcServerConfigState.CONNECTED);

        ircServerConfig = ircServerConfigService.save(ircServerConfig);
        System.out.printf("IrcServerConfig created: %s\n\n", ircServerConfig.toString());

        System.out.printf("Creating Channels ...\n");
        String[] channelNames = answers.get(ConfigurationItems.CHANNELS_TO_JOIN).split(",");

        for (String channelName : channelNames) {
            Channel channel = new Channel(network, channelName);
            channel.setChannelFlags("");
            channel.setChannelState(ChannelState.NONE);
            channel.setChannelStartupState(ChannelStartupState.JOIN);
            channel = channelService.save(channel);
            System.out.printf("Channel created: %s\n", channel.toString());
        }
        return true;
    }

    private boolean confirmAnswers(Map<ConfigurationItems, String> answers) {
        for (ConfigurationItems item : ConfigurationItems.values()) {
            System.out.printf("%25s : %s\n", item, answers.get(item));
        }
        System.out.println("\n\nConfiguration ok, answer yes/no?");
        String answer = sc.nextLine();
        if (answer.toLowerCase().equals("yes")) {
            return true;
        }
        return false;
    }

    private Map<ConfigurationItems, String> configureItems() {
        System.out.println("\n\n -- Configure -- \n\n");

        Map<ConfigurationItems, String> answers = new HashMap<>();
        boolean configOk = false;
        while (!configOk) {
            for (ConfigurationItems item : ConfigurationItems.values()) {
                System.out.printf("[%s] %s?\n", item.getDefaults(), item.getDescription());
                String answer = sc.nextLine();
                if (answer.length() == 0) {
                    answer = item.getDefaults();
                }
                answers.put(item, answer);
            }
            configOk = confirmAnswers(answers);
        }
        return answers;
    }

    public static void main(String[] args) {
        ConfigurationInitImpl instance = new ConfigurationInitImpl();
        instance.configureItems();
    }
}
