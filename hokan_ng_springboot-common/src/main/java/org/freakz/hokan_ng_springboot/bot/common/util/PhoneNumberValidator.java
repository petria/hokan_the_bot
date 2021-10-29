package org.freakz.hokan_ng_springboot.bot.common.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PhoneNumberValidator {


    public static boolean isValidE123(String s) {
        String regex = "^\\+?[0-9. ()-]{10,25}$";
        Pattern p = Pattern.compile(regex);

        Matcher m = p.matcher(s);
        return (m.find() && m.group().equals(s));
    }

    public static void main(String[] args) {
        String phone1 = "+91 3423 546443";
        String phone2 = "+44 343 2324";
        String phone3 = "91 4354 3454";
        String[] phoneNumbers = {"0977555541", phone2, phone3, "04577345641"};

        for (int i = 0; i < phoneNumbers.length; i++) {
            String phoneNumber = phoneNumbers[i];
            if (isValidE123(phoneNumber))
                System.out.print(phoneNumber + " is valid phone number");
            else
                System.out.print(phoneNumber + " is invalid Phone number");

            System.out.println();
        }
    }

}
