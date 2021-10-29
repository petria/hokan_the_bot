package org.freakz.hokan_ng_springboot.bot.engine.command.handlers;

import com.martiansoftware.jsap.*;
import org.freakz.hokan_ng_springboot.bot.common.events.EngineResponse;
import org.freakz.hokan_ng_springboot.bot.common.events.InternalRequest;
import org.freakz.hokan_ng_springboot.bot.common.exception.HokanException;
import org.freakz.hokan_ng_springboot.bot.common.jpa.entity.User;
import org.freakz.hokan_ng_springboot.bot.common.jpa.entity.UserChannel;
import org.freakz.hokan_ng_springboot.bot.engine.command.HelpGroup;
import org.freakz.hokan_ng_springboot.bot.engine.command.annotation.HelpGroups;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import static org.freakz.hokan_ng_springboot.bot.common.util.StaticStrings.*;

/**
 * User: petria
 * Date: 12/18/13
 * Time: 11:15 AM
 *
 * @author Petri Airio <petri.j.airio@gmail.com>
 */
@Component

@Scope("prototype")
@HelpGroups(
        helpGroups = {HelpGroup.ACCESS_CONTROL, HelpGroup.USERS}
)
public class UserModCmd extends Cmd {

    public UserModCmd() {
        super();
        setHelp("Modify user information.");

        Switch sw = new Switch(ARG_VERBOSE)
                .setShortFlag('v');
        registerParameter(sw);

        FlaggedOption flg = new FlaggedOption(ARG_EMAIL)
                .setRequired(false)
                .setLongFlag("email")
                .setShortFlag('e');
        registerParameter(flg);


        flg = new FlaggedOption(ARG_FULL_NAME)
                .setRequired(false)
                .setLongFlag("fullname")
                .setShortFlag('n');
        registerParameter(flg);

        flg = new FlaggedOption(ARG_JOIN_MSG)
                .setRequired(false)
                .setLongFlag("joinmsg")
                .setShortFlag('j');
        registerParameter(flg);

        flg = new FlaggedOption(ARG_MASK)
                .setRequired(false)
                .setLongFlag("mask")
                .setShortFlag('m');
        registerParameter(flg);

        flg = new FlaggedOption(ARG_PHONE)
                .setRequired(false)
                .setLongFlag("phone")
                .setShortFlag('p');
        registerParameter(flg);

        flg = new FlaggedOption(ARG_TELEGRAM_ID)
                .setStringParser(JSAP.INTEGER_PARSER)
                .setDefault("0")
                .setRequired(false)
                .setLongFlag("telegram")
                .setShortFlag('t');
        registerParameter(flg);

        UnflaggedOption opt = new UnflaggedOption(ARG_NICK)
                .setRequired(false)
                .setGreedy(false);
        registerParameter(opt);

    }


    @Override
    public void handleRequest(InternalRequest request, EngineResponse response, JSAPResult results) throws HokanException {
        String mask = results.getString(ARG_MASK);
        String target = results.getString(ARG_NICK, "me");
        String email = results.getString(ARG_EMAIL);
        String flags = results.getString(ARG_FLAGS);
        String fullName = results.getString(ARG_FULL_NAME);
        String joinMsg = results.getString(ARG_JOIN_MSG);
        String phone = results.getString(ARG_PHONE);
        int telegramID = results.getInt(ARG_TELEGRAM_ID);

        User hUser;
        if (target.equals("me")) {
            hUser = request.getUser();
        } else {
            if (accessControlService.isAdminUser(request.getUser())) {
                hUser = userService.findFirstByNick(target);
            } else {
                response.addResponse("Only Admins can modify others data!");
                return;
            }
        }

        if (hUser == null) {
            response.addResponse("No User found with: " + target);
            return;
        }
        UserChannel userChannel = userChannelService.getUserChannel(hUser, request.getChannel());

        String ret = "";
        boolean updateUserChannel = false;
        int oldTelegramID = hUser.getTelegramID();
        if (oldTelegramID != telegramID) {
            hUser.setTelegramID(telegramID);
            ret += "TgramID  : '" + oldTelegramID + "' -> '" + telegramID + "'\n";
        }
        if (email != null) {
            String old = hUser.getEmail();
            hUser.setEmail(email);
            ret += "Email    : '" + old + "' -> '" + email + "'\n";

        }
        if (fullName != null) {
            String old = hUser.getFullName();
            hUser.setFullName(fullName);
            ret += "FullName : '" + old + "' -> '" + fullName + "'\n";
        }
        if (joinMsg != null) {
            String old = userChannel.getJoinComment();
            userChannel.setJoinComment(joinMsg);
            ret += "JoinMsg  : '" + old + "' -> '" + joinMsg + "'\n";
            updateUserChannel = true;
        }
        if (mask != null) {
            String old = hUser.getMask();
            hUser.setMask(mask);
            ret += "Mask     : '" + old + "' -> '" + mask + "'\n";
        }
        if (phone != null) {
            String old = hUser.getPhone();
            hUser.setPhone(phone);
            ret += "Phone  : '" + old + "' -> '" + phone + "'\n";
        }

        if (ret.length() > 0) {
            hUser = userService.save(hUser);
            if (updateUserChannel) {
                userChannelService.save(userChannel);
            }
            if (results.getBoolean(ARG_VERBOSE)) {
                response.addResponse(hUser.getNick() + " datas modified: \n" + ret);
            } else {
                response.addResponse("Modified!");
            }
        } else {
            response.addResponse("Nothing modified!");
        }
    }

}
