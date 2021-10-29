package org.freakz.hokan_ng_springboot.bot.common.util;

/**
 * User: petria
 * Date: 25-Mar-2009
 * Time: 23:04:11
 *
 * @author Petri Airio <petri.j.airio@gmail.com>
 */

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Splits an IRC line up that would be too long otherwise.
 * <p>
 * License: Creative Commons Share Alike Attribution (CC-BY-SA)
 * http://creativecommons.org/licenses/by-sa/3.0/
 * Attribution: List A.McBain as original author.
 *
 * @author A.McBain
 */
public class IRCUtility {

    // Total length for an IRC protocol (minus \r\n line endings)
    public static final int PROTOCOL_LENGTH = 510;
    // Total allowed length for hostnames (believed to be accurate).
    public static final int HOSTNAME_LENGTH = 63;
    // Total length for a command. Longest I found was 7, 1 char buffer.
    public static final int COMMAND_LENGTH = 8;
    // Extra bits of protocol. 2 spaces, 1 : character.
    public static final int PROTOCOL_ADJUST_LENGTH = 4;
    // The total allowed nick length.
    public static int NICK_LENGTH = 16;
    // The total allowed channel length.
    public static int CHANNEL_LENGTH = 30;

    // Get the approximate maximum size a message can be, accounting for protocol length.
    public static int getIRCReceivingMessageLengthInBytes(int chanlen, int nicklen) {
        return (PROTOCOL_LENGTH - HOSTNAME_LENGTH - nicklen - chanlen - COMMAND_LENGTH - PROTOCOL_ADJUST_LENGTH);
    }

    // Get the approximate max size using the following channel length.
    public static int getChannelIRCReceivingMessageLengthInBytes(String channel) {
        return (getIRCReceivingMessageLengthInBytes(channel.length(), NICK_LENGTH));
    }

    // Get the approximate max size using the following nick length.
    public static int getUserIRCReceivingMessageLengthInBytes(String user) {
        return (getIRCReceivingMessageLengthInBytes(CHANNEL_LENGTH, user.length()));
    }

    // Breaks up a message into multiple lines based on the current (estimated) line length.
    public static String[] breakUpMessageByIRCLineLength(String target, String message) {
        int byteLength;
        // Line byte length.
        if (target.startsWith("#")) {
            byteLength = getChannelIRCReceivingMessageLengthInBytes(target);
        } else {
            byteLength = getUserIRCReceivingMessageLengthInBytes(target);
        }

        // Store all of our lines into which we've split the message.
        List<String> lines = new ArrayList<String>();
        // All the "words" in this message.
        List<String> words = Arrays.asList(message.split("(?=[\\s])"));

        // The current StringBuilder.
        StringBuilder builder = new StringBuilder();
        // The current number of bytes in the StringBuilder.
        int byteCount = 0;

        // Loop over all the words.
        for (int i = 0; i < words.size(); i++) {
            int bytes = 0;
            try {
                // Get number of bytes in utf-8
                bytes = words.get(i).getBytes("utf-8").length;
            } catch (UnsupportedEncodingException e) {
                // Backup plan
                bytes = words.get(i).getBytes().length;
            }
            // If this would exceed our line byte-limit, start a new line.
            if (byteCount > 0 && byteCount + bytes > byteLength) {
                lines.add(builder.toString());
                builder = new StringBuilder();
                byteCount = 0;
                i--; // Back-up one.
            } else if (byteCount == 0 && bytes > byteLength) {
                StringBuilder word = new StringBuilder(words.get(i));
                // Add as many as we can.
                for (int j = 0; j < byteLength - builder.length(); j++) {
                    builder.append(word.charAt(0));
                    word = word.deleteCharAt(0);
                }
                try {
                    // Add this as the next word, so the remaining bits get added.
                    words.add(i + 1, word.toString());

                } catch (UnsupportedOperationException e) {
                    words.set(i, word.toString());
                    i--; // back up one, we need to re-process what is left of this one.
                }
                byteCount += word.length();
            } else {
                // Add word.
                builder.append(words.get(i));
                byteCount += bytes;
            }
        }

        // Add last builder.
        lines.add(builder.toString());

        return (lines.toArray(new String[lines.size()]));
    }

}
