package org.freakz.hokan_ng_springboot.bot.common.jpa.service;

import org.freakz.hokan_ng_springboot.bot.common.jpa.entity.PropertyEntity;
import org.freakz.hokan_ng_springboot.bot.common.jpa.entity.PropertyName;
import org.freakz.hokan_ng_springboot.bot.common.jpa.repository.PropertyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Petri Airio on 27.3.2015.
 */
@Service

public class PropertyRepositoryService extends PropertyBase implements PropertyService {

    private static final Logger log = LoggerFactory.getLogger(PropertyRepositoryService.class);


    @Autowired
    private PropertyRepository repository;

    @Override
    public List<PropertyEntity> findAll() {
        return repository.findAll();
    }

    @Override
    public PropertyEntity save(PropertyEntity property) {
        return repository.save(property);
    }

    @Override
    public void delete(PropertyEntity object) {
        repository.delete(object);
    }

    @Override
    public PropertyEntity findFirstByPropertyName(PropertyName propertyName) {
        return repository.findFirstByPropertyName(propertyName);
    }

    @Override
    public long getNextPid() {
        PropertyEntity property = repository.findFirstByPropertyName(PropertyName.PROP_SYS_PID_COUNTER);
        if (property == null) {
            property = new PropertyEntity(PropertyName.PROP_SYS_PID_COUNTER, "1", "");
        }
        long pid = Long.parseLong(property.getValue());
        property.setValue("" + (pid + 1));
        repository.save(property);
        return pid;
    }

}
