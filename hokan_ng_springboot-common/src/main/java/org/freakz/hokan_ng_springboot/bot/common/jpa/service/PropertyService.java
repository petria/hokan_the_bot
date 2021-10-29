package org.freakz.hokan_ng_springboot.bot.common.jpa.service;

import org.freakz.hokan_ng_springboot.bot.common.jpa.entity.PropertyEntity;
import org.freakz.hokan_ng_springboot.bot.common.jpa.entity.PropertyName;

import java.util.List;

/**
 * Created by Petri Airio on 27.3.2015.
 */
public interface PropertyService {

    List<PropertyEntity> findAll();

    PropertyEntity save(PropertyEntity property);

    void delete(PropertyEntity object);

    PropertyEntity findFirstByPropertyName(PropertyName propertyName);

    String getPropertyAsString(PropertyName propertyName, String defaultValue);

    int getPropertyAsInt(PropertyName propertyName, int defaultValue);

    long getPropertyAsLong(PropertyName propertyName, long defaultValue);

    boolean getPropertyAsBoolean(PropertyName propertyName, boolean defaultValue);

    long getNextPid();

}
