package org.freakz.hokan_ng_springboot.bot.common.exception;

import java.io.Serializable;

/**
 * Date: 3.6.2013
 * Time: 13:25
 *
 * @author Petri Airio (petri.j.airio@gmail.com)
 */
public class HokanException extends Exception implements Serializable {

    private static final long serialVersionUID = 1L;

    public HokanException() {
    }

    public HokanException(Exception t) {
        super(t);
    }

    public HokanException(String message) {
        super(message);
    }

    public HokanException(String message, Throwable cause) {
        super(message, cause);
    }


}
