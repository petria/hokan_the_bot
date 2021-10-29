package org.freakz.hokan_ng_springboot.bot.common.events;

import java.io.Serializable;

/**
 * User: petria
 * Date: 11/8/13
 * Time: 4:20 PM
 *
 * @author Petri Airio <petri.j.airio@gmail.com>
 */
public class EngineMethodCall implements Serializable {

    private static final long serialVersionUID = 1L;

    private String methodName;
    private String[] methodArgs;

    public EngineMethodCall() {
    }

    public EngineMethodCall(String methodName, String[] methodArgs) {
        this.methodName = methodName;
        this.methodArgs = methodArgs;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String[] getMethodArgs() {
        return methodArgs;
    }

    public void setMethodArgs(String[] methodArgs) {
        this.methodArgs = methodArgs;
    }
}
