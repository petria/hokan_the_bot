package org.freakz.hokan_ng_springboot.bot.common.exception;

/**
 * Created by Petri Airio (petri.j.airio@gmail.com) 20/09/16 / 08:49
 */
public class HokanRuntimeException extends RuntimeException {

    final long serialVersionUID = 1L;

    public HokanRuntimeException() {
    }

    public HokanRuntimeException(String message) {
        super(message);
    }

    public HokanRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}
