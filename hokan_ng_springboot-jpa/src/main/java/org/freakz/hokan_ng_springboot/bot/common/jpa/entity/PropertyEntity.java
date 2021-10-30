package org.freakz.hokan_ng_springboot.bot.common.jpa.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * User: petria
 * Date: 2/19/12
 * Time: 4:35 PM
 */
@Entity
@Table(name = "PROPERTIES")
public class PropertyEntity extends PropertyEntityBase implements Serializable {

    public PropertyEntity() {
    }

    public PropertyEntity(PropertyName propertyName, String value, String flags) {
        super(propertyName, value, flags);
    }

    public String toString() {
        return String.format("%s = %s", getPropertyName().toString(), getValue());
    }

}
