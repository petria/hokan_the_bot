package org.freakz.hokan_ng_springboot.bot.common.exception;

/**
 * User: petria
 * Date: 11/18/13
 * Time: 8:24 AM
 *
 * @author Petri Airio <petri.j.airio@gmail.com>
 */
public class HokanDAOException extends HokanException {

    public HokanDAOException(Exception t) {
        super(t);
    }

    public HokanDAOException(String message) {
        super(message);
    }

    public HokanDAOException(String message, Throwable cause) {
        super(message, cause);
    }

}
