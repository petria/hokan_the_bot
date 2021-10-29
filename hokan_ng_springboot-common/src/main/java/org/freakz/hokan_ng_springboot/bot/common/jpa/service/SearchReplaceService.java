package org.freakz.hokan_ng_springboot.bot.common.jpa.service;

import org.freakz.hokan_ng_springboot.bot.common.jpa.entity.SearchReplace;

import java.util.List;

/**
 * Created by Petri Airio on 31.8.2015.
 */
public interface SearchReplaceService {

    SearchReplace addSearchReplace(String sender, String search, String replace);

    List<SearchReplace> findAll();

    void deleteAll();

    void delete(SearchReplace sr);

    List<SearchReplace> findByTheSearch(String search);

    SearchReplace findOne(long id);

}
