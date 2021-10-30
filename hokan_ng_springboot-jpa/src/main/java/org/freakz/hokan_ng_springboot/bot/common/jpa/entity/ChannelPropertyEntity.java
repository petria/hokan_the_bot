package org.freakz.hokan_ng_springboot.bot.common.jpa.entity;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * User: petria
 * Date: 12/10/13
 * Time: 2:04 PM
 *
 * @author Petri Airio <petri.j.airio@gmail.com>
 */
@Entity
@Table(name = "CHANNEL_PROPERTIES")
public class ChannelPropertyEntity extends PropertyEntityBase implements Serializable {

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "CHANNEL", referencedColumnName = "ID", nullable = false)
    private Channel channel;

    public ChannelPropertyEntity() {
    }

    public ChannelPropertyEntity(Channel channel, PropertyName property, String value, String flags) {
        super(property, value, flags);
        this.channel = channel;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public String toString() {
        return String.format("[%s] %s = %s", channel.getChannelName(), getPropertyName().toString(), getValue());
    }

}
