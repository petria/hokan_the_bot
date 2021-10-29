package org.freakz.hokan_ng_springboot.bot.common.jpa.service;

import org.freakz.hokan_ng_springboot.bot.common.jpa.entity.Channel;
import org.freakz.hokan_ng_springboot.bot.common.jpa.entity.ChannelPropertyEntity;
import org.freakz.hokan_ng_springboot.bot.common.jpa.entity.ChannelState;
import org.freakz.hokan_ng_springboot.bot.common.jpa.entity.PropertyName;
import org.freakz.hokan_ng_springboot.bot.common.jpa.repository.ChannelPropertyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Petri Airio on 26.3.2015.
 */
@Service
public class ChannelPropertyRepositoryService extends PropertyBase implements ChannelPropertyService {

    @Autowired
    private ChannelPropertyRepository repository;

    @Override
    public List<ChannelPropertyEntity> findByChannel(Channel channel) {
        return repository.findByChannel(channel);
    }

    @Override
    @Transactional
    public ChannelPropertyEntity save(ChannelPropertyEntity newRow) {
        return repository.save(newRow);
    }

    @Override
    @Transactional
    public void delete(ChannelPropertyEntity object) {
        repository.delete(object);
    }

    @Override
    @Transactional
    public void deleteByChannel(Channel object) {
        repository.deleteByChannel(object);
    }

    @Override
    @Transactional
    public List<Channel> getChannelsWithProperty(PropertyName propertyName, String valueMatcher) {
        List<ChannelPropertyEntity> all = repository.findAll();
        List<ChannelPropertyEntity> properties = repository.findByPropertyName(propertyName);
        List<Channel> channels = new ArrayList<>();
        for (ChannelPropertyEntity property : properties) {
            String value = property.getValue();
            if (value != null) {
                if (value.matches(valueMatcher)) {
                    if (property.getChannel().getChannelState().equals(ChannelState.JOINED)) {
                        channels.add(property.getChannel());
                    }
                }
            }
        }
        return channels;
    }

    @Override
    public ChannelPropertyEntity setChannelProperty(Channel theChannel, PropertyName propertyName, String value) {
        ChannelPropertyEntity property = repository.findFirstByChannelAndPropertyName(theChannel, propertyName);
        if (property == null) {
            property = new ChannelPropertyEntity(theChannel, propertyName, value, "");
        } else {
            property.setValue(value);
        }
        return repository.save(property);
    }

    @Override
    public ChannelPropertyEntity findFirstByChannelAndPropertyName(Channel channel, PropertyName propertyName) {
        return repository.findFirstByChannelAndPropertyName(channel, propertyName);
    }

    @Override
    public boolean getChannelPropertyAsBoolean(Channel channel, PropertyName propertyName, boolean value) {
        return super.getChannelPropertyAsBoolean(channel, propertyName, value);
    }

    @Override
    public String getChannelPropertyAsString(Channel channel, PropertyName propertyName, String value) {
        return super.getChannelPropertyAsString(channel, propertyName, value);
    }

}
