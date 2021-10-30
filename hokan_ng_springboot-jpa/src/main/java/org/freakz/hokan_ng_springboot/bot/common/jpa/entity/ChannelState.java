package org.freakz.hokan_ng_springboot.bot.common.jpa.entity;

import java.io.Serializable;

/**
 * User: petria
 * Date: 11/7/13
 * Time: 12:05 PM
 *
 * @author Petri Airio <petri.j.airio@gmail.com>
 */
public enum ChannelState implements Serializable {

    NONE,
    BANNED,
    INVITED,
    JOINED,
    KICKED_OUT,
    NOT_JOINED,
    PARTED,

}
