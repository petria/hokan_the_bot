package org.freakz.hokan_ng_springboot.bot.services.service.wholelinetricker;

import lombok.extern.slf4j.Slf4j;
import org.freakz.hokan_ng_springboot.bot.common.enums.HokanModule;
import org.freakz.hokan_ng_springboot.bot.common.events.IrcMessageEvent;
import org.freakz.hokan_ng_springboot.bot.common.events.NotifyRequest;
import org.freakz.hokan_ng_springboot.bot.common.jms.api.JmsSender;
import org.freakz.hokan_ng_springboot.bot.common.models.TimeDifferenceData;
import org.freakz.hokan_ng_springboot.bot.common.util.StringStuff;
import org.freakz.hokan_ng_springboot.bot.common.util.Uptime;
import org.freakz.hokan_ng_springboot.bot.services.service.timeservice.TimeDifferenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * User: petria
 * Date: 21-Jan-2009
 * Time: 08:22:02
 */
@Service
@Slf4j
@Scope("singleton")
public class WholeLineTriggersImpl implements WholeLineTriggers {

    private final JmsSender jmsSender;
    private final TimeDifferenceService timeDifferenceService;

    @Autowired
    public WholeLineTriggersImpl(JmsSender jmsSender, TimeDifferenceService timeDifferenceService) {
        this.jmsSender = jmsSender;
        this.timeDifferenceService = timeDifferenceService;
    }

    private String _olpo = "";

    private void checkPilalla(IrcMessageEvent iEvent) {
        int rnd = 1 + (int) (Math.random() * 100);
        if (rnd < 85) {
            return;
        }
        String line = iEvent.getMessage().toLowerCase();
        if (line.contains("pilalla")) {
            processReply(iEvent, _olpo + iEvent.getSender() + ": pelkkää paskaa tilalla!");
        }
    }

    //    TTR0gVk001

    private void checkPyksyCrypto(IrcMessageEvent iEvent) {
/*        int rnd = 1 + (int) (Math.random() * 100);
        if (rnd < 55) {
            return;
        }*/
        String pornot = StringStuff.getRandomString("RemonttiMiesPornot/", "KääpiöPornot/", "HomoPornot/", "EläinPornot/", "SaapasPornot/");
        String line = iEvent.getMessage();
        if (line.contains("TTR0gVk001")) {
            processReply(iEvent, _olpo + iEvent.getSender() + ": " + pornot + " unlocked!");
        }

    }

    public void checkPallo(IrcMessageEvent iEvent) {
        int rnd = 1 + (int) (Math.random() * 100);
        log.debug("rnd: {}", rnd);
        if (rnd > 20) {
            return;
        }
        String line = iEvent.getMessage();
        if (line.matches("^\\d?-\\d?")) {
            String[] split = line.split("-");
            int i1 = Integer.parseInt(split[0]);
            int i2 = Integer.parseInt(split[1]);
            String win = String.format("%d-%d=%d", i1, i2, i1 - i2);
            processReply(iEvent, _olpo + iEvent.getSender() + ": " + win);
        }
    }

    private void checkYhdet(IrcMessageEvent iEvent) {

        String line = iEvent.getMessage().toLowerCase();
        if (line.contains("yhdet") || line.contains("yhdelle") || line.contains("yhelle") || line.contains("leskiseen") || line.contains("leskinen")) {

            if (iEvent.getSender().equals("shd")) {
                processReply(iEvent, _olpo + iEvent.getSender() + ": (56)");
                return;
            }

            int rnd = 1 + (int) (Math.random() * 100);
            if (rnd < 33) {
                return;
            }

            rnd = 8 + (int) (Math.random() * 20);
            processReply(iEvent, _olpo + iEvent.getSender() + ": (" + rnd + ")");

        }
    }


    private void checkPerkeleVittu(IrcMessageEvent iEvent) {
        int rnd = 1 + (int) (Math.random() * 100);
        if (rnd < 85) {
            return;
        }
        String line = iEvent.getMessage().toLowerCase();
        if (line.contains("perkele")) {
            processReply(iEvent, _olpo + "PERKELE!");
        }
        if (line.contains("vittu")) {
            processReply(iEvent, _olpo + "(|)");
        }

    }

    private void checkHuomenta(IrcMessageEvent iEvent) {
        String line = iEvent.getMessage().toLowerCase();
        String reply = null;
        if (line.startsWith("aamut") ||
                line.startsWith("aamua") ||
                line.startsWith("huomenta") ||
                line.startsWith("huomenia") ||
                line.startsWith("houm") ||
                line.startsWith("ment") ||
                line.startsWith("hooment") ||
                line.startsWith("suolenta") ||
                line.startsWith("menta")) {

            Calendar cal = Calendar.getInstance();
            int hour = cal.get(Calendar.HOUR_OF_DAY);
            if (hour >= 6 && hour <= 10) {
                if (line.startsWith("suolenta")) {
                    reply = "*PRÖÖÖT*";
                } else {
                    reply = "!!!!";
                }
            } else if (hour > 10 && hour <= 16) {
                reply = "Päivää!";
            } else if (hour > 16 && hour <= 23) {
                reply = "Iltaa!";
            } else if (hour > 23) {
                reply = "Yotä!";
            }
        }
        if (reply != null) {
            processReply(iEvent, _olpo + reply);
        }

    }

    private int jouluRandomBase = 65;

    public LocalDateTime getJouluTime(LocalDateTime now) {
        LocalDateTime start = LocalDateTime.of(now.getYear(), 12, 24, 12, 0);
        LocalDateTime end = LocalDateTime.of(now.getYear(), 12, 31, 23, 59);
        LocalDateTime jouluTime;
        if (now.isAfter(start) && now.isBefore(end)) {
            // year end!
            jouluTime = LocalDateTime.of(now.getYear() + 1, 12, 24, 12, 0);
        } else {
            jouluTime = LocalDateTime.of(now.getYear(), 12, 24, 12, 0);

        }
        return jouluTime;
    }

    public boolean isVäliPäivä() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = LocalDateTime.of(now.getYear(), 12, 24, 12, 0);
        LocalDateTime end = LocalDateTime.of(now.getYear(), 12, 31, 23, 59);
        return now.isAfter(start) && now.isBefore(end);
    }

    public void checkJoulu(IrcMessageEvent iEvent) {

        String line = iEvent.getMessage();

        int rnd = 1 + (int) (Math.random() * 100);
        if (line.contains("joulu") && rnd > jouluRandomBase) {


            if (isVäliPäivä()) {
                processReply(iEvent, _olpo + "Tän vuaren joulu meni jo!");
                return;
            }

            LocalDateTime now = LocalDateTime.now();
            LocalDateTime jouluTime = getJouluTime(now);
            TimeDifferenceData timeDifference = timeDifferenceService.getTimeDifference(now, jouluTime);
            long[] ut = timeDifference.getDiffs();


            boolean b1 = false;
            String monthPart;
            if (ut[4] > 0) {
                String month;
                if (ut[4] > 1) {
                    month = "kuukautta";
                } else {
                    month = "kuukausi";
                }
                monthPart = String.format("%d %s ", ut[4], month);
                b1 = true;
            } else {
                monthPart = "";
            }

            boolean b2 = false;
            String dayPart;
            if (ut[3] > 0) {
                String day;
                if (ut[3] > 1) {
                    day = "päivää";
                } else {
                    day = "päivä";
                }
                dayPart = String.format("%d %s ", ut[3], day);
                b2 = true;
            } else {
                dayPart = "";
            }

            String andPart;
            if (b1 || b2) {
                andPart = "ja ";
            } else {
                andPart = "";
            }

            String ret = String.format("%s%s%s%02d:%02d:%02d jouluun!", monthPart, dayPart, andPart, ut[2], ut[1], ut[0]);

            processReply(iEvent, _olpo + ret);
            jouluRandomBase = 320;

        } else {
            jouluRandomBase--;
        }
    }

    private int juhannusRandomBase = 65;

    private void checkJuhannus(IrcMessageEvent iEvent) {

        String line = iEvent.getMessage();

        int rnd = 1 + (int) (Math.random() * 100);
        if (line.contains("juhannus") && rnd > juhannusRandomBase) {
            String[] format = {"00", "00", "00", "0"};

            GregorianCalendar cal = (GregorianCalendar) Calendar.getInstance();
            cal.set(Calendar.MONTH, Calendar.JUNE);
            cal.set(Calendar.DAY_OF_MONTH, 21);
            cal.set(Calendar.YEAR, 2019);
            cal.set(Calendar.HOUR_OF_DAY, 12);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);

            Uptime uptime = new Uptime(cal.getTime().getTime());
            Integer[] ut = uptime.getTimeDiff();

            String ret = StringStuff.fillTemplate("%3 päivää ja %2:%1:%0 juhannukseen!", ut, format);

            processReply(iEvent, _olpo + ret);
            jouluRandomBase = 120;

        } else {
            juhannusRandomBase--;
        }
    }


    private String[] splitByWord(String line) {
        int idx = line.indexOf(" ");
        if (idx != -1) {
            line = line.substring(idx);
            return line.split("vai");
        }
        return null;
    }

    private void checkPitasko(IrcMessageEvent iEvent) {
        String msg = iEvent.getMessage();
        if (StringStuff.match(iEvent.getMessage(), "pit.i?sk..*", true)) {
            StringBuilder sb = new StringBuilder();
            int rndLevel = 500;
            int rnd = (int) (Math.random() * 1000);

            if (iEvent.getMessage().toLowerCase().matches(".*(glugga).*")) {
                rndLevel = 6000;
            } else {
                if (iEvent.getMessage().toLowerCase().matches(".*(viina|viini|bisse|olut|kalja|huikka|ryypätä|pillu|panna|perse).*")) {
                    rndLevel = 5000;
                }
                if (iEvent.getMessage().toLowerCase().indexOf("linux") > 0) {
                    rndLevel = 4000;
                }
                if (iEvent.getMessage().toLowerCase().matches(".*(mac|osx|win).*")) {
                    rndLevel = 3000;
                }
            }

            String rndWord = "";
            if (msg.contains("vai")) {
                String[] split = splitByWord(msg);
                rndWord = " " + StringStuff.getRandomString(split).trim() + " ";
            }


            if (rndLevel == 6000) {
                sb.append(String.format("Joo, pitäis se on #AmigaFIN laki!!!!", rndWord));
            } else if (rndLevel == 5000) {
                sb.append(String.format("Joo pitäis%s!!!!!!", rndWord));
            } else if (rndLevel == 4000) {
                sb.append(String.format("Joo pitäis%s!!!", rndWord));
            } else if (rndLevel == 3000) {
                sb.append("EI pitäis!!!");
            } else if (rnd > rndLevel) {
                sb.append(String.format("Joo pitäis%s!", rndWord));
            } else {
                sb.append("Ei pitäis!!");
            }
            processReply(iEvent, _olpo + sb.toString());

        }

    }

    private void checkSpede(IrcMessageEvent iEvent) {
        int rndLevel = 400;
        int rnd = (int) (Math.random() * 1000);

        if (rnd > rndLevel && iEvent.getMessage().toLowerCase().contains("spede")) {
//            processReply(iEvent, _olpo + Colors.BOLD + "*voi rähmä - spedeläps*");
        }

    }

    private void checkSilli(IrcMessageEvent iEvent) {
        int rndLevel = 850;
        int rnd = (int) (Math.random() * 1000);
        String msg = iEvent.getMessage().toLowerCase();
        boolean kala = false;
        if (msg.contains("kala")) {
            kala = true;
        } else if (msg.contains("silli")) {
            kala = true;
        } else if (msg.contains("hauki")) {
            kala = true;
        } else if (msg.contains("ahven")) {
            kala = true;
        } else if (msg.contains("puikko")) {
            kala = true;
        } else if (msg.contains("turska")) {
            kala = true;
        }

        if (rnd > rndLevel && kala) {
//            processReply(iEvent, _olpo + Colors.BOLD + "*2KG SIIKA!*");
        }
    }

    public void checkDrugs(IrcMessageEvent iEvent) {
        int rndLevel = 200;
        int rnd = (int) (Math.random() * 1000);
        if (rnd > rndLevel) {
            String msg = iEvent.getMessage().toLowerCase();
            if (msg.matches(".*(huume|huumeita|humei|piri|esso|buda|vauhti|hasis|kukka|lakka).*")) {
                String randomDruk = StringStuff.getRandomString("piri", "esso", "hepo", "vauhti", "buda", "hasis", "lakka", "kukka");
                int randomPrice = 10 + (int) (Math.random() * 100);
                String randomUnit = StringStuff.getRandomString("kg", "g", "ug", "mg", "kilo");
                String randomMani = StringStuff.getRandomString("markkaa", "euroa", "mk", "€", "£", "$");
                String reply = String.format("%s: %s nyt vain %d%s/%s!", iEvent.getSender(), randomDruk, randomPrice, randomMani, randomUnit);
                processReply(iEvent, _olpo + reply);
            }
        }
    }

    private void checkOlisko(IrcMessageEvent iEvent) {
        String msg = iEvent.getMessage().toLowerCase();
        if (msg.matches("olisko .*|oliskohan .*")) {
            int rnd = (int) (Math.random() * 1000);
            if (rnd > 500) {
                processReply(iEvent, _olpo + "Joo olis!");
            } else {
                processReply(iEvent, _olpo + "Ei olis!");
            }

        }
    }

    private void checkVTEC(IrcMessageEvent iEvent) {
        String msg = iEvent.getMessage().toLowerCase();
        if (msg.contains("vtec")) {
            processReply(iEvent, _olpo + iEvent.getSender() + ": VTEC YO!");
        }
    }


    private final static String[] stonez1 = {"kivi", "paperi", "sakset"};
    private final static String[] stonez2 = {"paperi", "sakset", "kivi"};

    private void checkStonePaper(IrcMessageEvent iEvent) {
        String msg = iEvent.getMessage().toLowerCase();
        int fndIdx = -1;
        if (msg.equals(stonez1[0])) {
            fndIdx = 0;
        }
        if (msg.equals(stonez1[1])) {
            fndIdx = 1;
        }
        if (msg.equals(stonez1[2])) {
            fndIdx = 2;
        }

        if (fndIdx != -1) {
            int rnd = (int) (Math.random() * 1000) % 3;
            String my = stonez1[rnd];
            String reply = String.format("%s -> mulle %s - ", msg, my);
            if (msg.equals(my)) {
                reply += "tasapeli!!";
            } else if (my.equals(stonez2[fndIdx])) {
                reply += "voitin!";
            } else {
                reply += "hävisin!";

            }
            processReply(iEvent, _olpo + iEvent.getSender() + ": " + reply);
        }

    }


    private void checkMuisti(IrcMessageEvent iEvent) {
        String msg = iEvent.getMessage().toLowerCase();
        if (msg.startsWith("muista")) {
            processReply(iEvent, _olpo + iEvent.getSender() + ": muistissa!");
        }
    }

    private void processReply(IrcMessageEvent iEvent, String reply) {

        NotifyRequest notifyRequest = new NotifyRequest();
        notifyRequest.setNotifyMessage(reply);
        notifyRequest.setTargetChannelId(iEvent.getChannelId());
        jmsSender.send(HokanModule.HokanServices, HokanModule.HokanIo.getQueueName(), "WHOLE_LINE_TRIGGER_NOTIFY_REQUEST", notifyRequest, false);
    }

    private static long _lastReply = 0;

    @Override
    public void checkWholeLineTrigger(IrcMessageEvent iEvent) {
        long now = new Date().getTime();
        long diff = now - _lastReply;

//        checkJospa(iEvent);
//        checkDrugs(iEvent);
        checkPilalla(iEvent);
//        checkPerkeleVittu(iEvent);
        checkJoulu(iEvent);
//        checkJuhannus(iEvent);
        checkPitasko(iEvent);
//        checkSpede(iEvent);
//        checkSilli(iEvent);
//        checkOlisko(iEvent);
//        checkStonePaper(iEvent);
//        checkMuisti(iEvent);
//        checkVTEC(iEvent);
        checkYhdet(iEvent);
        checkPyksyCrypto(iEvent);
        checkPallo(iEvent);

        if (diff > 1500) {
            checkHuomenta(iEvent);
            _lastReply = now;
        }
    }


}
