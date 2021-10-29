package org.freakz.hokan_ng_springboot.bot.common.cmdpool;

import org.freakz.hokan_ng_springboot.bot.common.exception.HokanException;

/**
 * User: petria
 * Date: 11/5/13
 * Time: 12:06 PM
 *
 * @author Petri Airio <petri.j.airio@gmail.com>
 */
public interface CommandRunnable {

    void handleRun(long myPid, Object args) throws HokanException;

}
