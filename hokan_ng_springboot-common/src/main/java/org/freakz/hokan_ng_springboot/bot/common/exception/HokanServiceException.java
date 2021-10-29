package org.freakz.hokan_ng_springboot.bot.common.exception;

/**
 * User: petria
 * Date: 11/18/13
 * Time: 8:25 AM
 *
 * @author Petri Airio <petri.j.airio@gmail.com>
 */
public class HokanServiceException extends HokanException {

    public HokanServiceException(Exception t) {
        super(t);
    }

    public HokanServiceException(String message) {
        super(message);
    }

    public HokanServiceException(String message, Throwable cause) {
        super(message, cause);
    }

}
