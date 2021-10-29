package org.freakz.hokan_ng_springboot.bot.common.util;

import org.freakz.hokan_ng_springboot.bot.common.enums.HostOS;
import org.junit.Test;

/**
 * Created by petria on 17/08/16.
 * -
 */
public class HostOsDetectorTest {

    @Test
    public void testOsDetector() {
        HostOsDetector detector = new HostOsDetector();
        HostOS hostOs = detector.detectHostOs();
        int foo = 0;
    }

}
