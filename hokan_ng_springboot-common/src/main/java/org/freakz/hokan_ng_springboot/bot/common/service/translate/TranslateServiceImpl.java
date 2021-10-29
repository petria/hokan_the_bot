package org.freakz.hokan_ng_springboot.bot.common.service.translate;


import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: petria
 * Date: 12/14/13
 * Time: 9:07 AM
 *
 * @author Petri Airio <petri.j.airio@gmail.com>
 */
@Service
public class TranslateServiceImpl implements TranslateService {

//  @Autowired
//  ApplicationContext context;

    private static final String FI_ENG_BASE_URL = "http://ilmainensanakirja.fi/sanakirja_suomi-englanti/";
    private static final String FI_ENG_REGEXP = "<td valign=\"top\" id=\"searchResultNum\">.*?</td> <td>   <h3>    <a href=\"/sanakirja_englanti-suomi/.*?\">(.*?)</a>";

    private static final String ENG_FI_BASE_URL = "http://ilmainensanakirja.fi/sanakirja_englanti-suomi/";
    private static final String ENG_FI_REGEXP = "<td valign=\"top\" id=\"searchResultNum\">.*?</td> <td>   <h3>    <a href=\"/sanakirja_suomi-englanti/.*?\">(.*?)</a>";


    private List<String> doTranslate(String baseUrl, String regexp, String keyword) {
        List<String> results = new ArrayList<String>();
        try {
//      HttpPageFetcher httpPageFetcher = context.getBean(HttpPageFetcher.class);
//      httpPageFetcher.fetch(baseUrl + keyword, "UTF-8"); TODO

            String html = ""; // httpPageFetcher.getHtmlBuffer().toString();
            html = html.replaceAll("\n|\r", "");

            Pattern pattern = Pattern.compile(regexp);
            Matcher matcher = pattern.matcher(html);
            while (matcher.find()) {
                results.add(matcher.group(1));
            }
        } catch (Exception ex) {
            // ignore
        }

        return results;
    }

    @Override
    public List<String> translateFiEng(String keyword) {
        return doTranslate(FI_ENG_BASE_URL, FI_ENG_REGEXP, keyword);
    }

    @Override
    public List<String> translateEngFi(String keyword) {
        return doTranslate(ENG_FI_BASE_URL, ENG_FI_REGEXP, keyword);
    }
}
