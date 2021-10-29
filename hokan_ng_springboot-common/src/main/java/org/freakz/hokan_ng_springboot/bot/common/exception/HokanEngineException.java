package org.freakz.hokan_ng_springboot.bot.common.exception;

import java.io.Serializable;

/**
 * User: petria
 * Date: 11/18/13
 * Time: 9:18 AM
 *
 * @author Petri Airio <petri.j.airio@gmail.com>
 */
public class HokanEngineException extends HokanException implements Serializable {

    private static final long serialVersionUID = 1L;

    private String exceptionClassName;

    public HokanEngineException() {
        super();
    }

    public HokanEngineException(Exception t, String exceptionClassName) {
        super(t);
        this.exceptionClassName = exceptionClassName;
    }

    public HokanEngineException(String message, String exceptionClassName) {
        super(message);
        this.exceptionClassName = exceptionClassName;
    }

    public HokanEngineException(String message, Throwable cause, String exceptionClassName) {
        super(message, cause);
        this.exceptionClassName = exceptionClassName;
    }

    public String getExceptionClassName() {
        return exceptionClassName;
    }

    public void setExceptionClassName(String exceptionClassName) {
        this.exceptionClassName = exceptionClassName;
    }
}
