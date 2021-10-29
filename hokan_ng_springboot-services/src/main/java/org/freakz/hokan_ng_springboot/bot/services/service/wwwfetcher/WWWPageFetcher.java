package org.freakz.hokan_ng_springboot.bot.services.service.wwwfetcher;

/**
 * Created by petria on 24/08/16.
 * -
 */
public interface WWWPageFetcher {

    String fetchWWWPageNoCache(String url, boolean useJavaScript);

    String fetchWWWPage(String url, boolean useJavaScript);

}
