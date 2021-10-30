package org.freakz.hokan_ng_springboot.bot.common.jpa.entity;

import javax.persistence.*;
import java.io.Serializable;

/**
 * User: petria
 * Date: 12/10/13
 * Time: 2:01 PM
 *
 * @author Petri Airio <petri.j.airio@gmail.com>
 */
@MappedSuperclass
public class PropertyEntityBase implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private long id;

    @Column(name = "PROPERTY_NAME")
    @Enumerated(EnumType.STRING)
    private PropertyName propertyName;

    @Column(name = "VALUE")
    private String value;

    @Column(name = "FLAGS")
    private String flags;

    public PropertyEntityBase() {
    }

    public PropertyEntityBase(PropertyName propertyName, String value, String flags) {
        this.propertyName = propertyName;
        this.value = value;
        this.flags = flags;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public PropertyName getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(PropertyName propertyName) {
        this.propertyName = propertyName;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getFlags() {
        return flags;
    }

    public void setFlags(String flags) {
        this.flags = flags;
    }
}
