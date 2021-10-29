package org.freakz.hokan_ng_springboot.bot.services.service.wwwfetcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Created by petria on 24/08/16.
 * -
 */
@Service
public class WWWPageFetcherImpl implements WWWPageFetcher {

    private static final Logger log = LoggerFactory.getLogger(WWWPageFetcherImpl.class);


    public WWWPageFetcherImpl() {
    }

    @Override
    public String fetchWWWPageNoCache(String url, boolean useJavaScript) {
        log.debug("Fetching url: {}", url);
        return null;
    }

    @Override
    public String fetchWWWPage(String url, boolean useJavaScript) {
        return null;
    }

    public static class CacheParameters {
        public String url;
        public boolean useJavaScript;
    }
}
