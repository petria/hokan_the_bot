package org.freakz.hokan_ng_springboot.bot.common.util;

import org.freakz.hokan_ng_springboot.bot.common.jpa.entity.PropertyName;
import org.freakz.hokan_ng_springboot.bot.common.jpa.service.PropertyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.*;

//import static org.freakz.hokan_ng_springboot.bot.common.util.StaticStrings.HTTP_USER_AGENT;

/**
 * <br>
 * <p>
 * User: petria<br>
 * Date: 29-Jun-2007<br>
 * Time: 12:53:37<br>
 * <p>
 */
@Component
@Scope("prototype")
public class HttpPostFetcher {

    @Autowired
    private PropertyService properties;

    private StringBuilder htmlBuffer;

    public HttpPostFetcher() {
    }

    public void fetch(String urlStr, String encoding, String... params) throws IOException {

        URL url = new URL(urlStr);
        String proxyHost = null;
        int proxyPort = -1;

        if (properties != null) {
            proxyHost = properties.getPropertyAsString(PropertyName.PROP_SYS_HTTP_PROXY_HOST, null);
            proxyPort = properties.getPropertyAsInt(PropertyName.PROP_SYS_HTTP_PROXY_PORT, -1);
        }

        URLConnection conn;
        if (proxyHost != null && proxyPort != -1) {
            SocketAddress address = new
                    InetSocketAddress(proxyHost, proxyPort);
            Proxy proxy = new Proxy(Proxy.Type.HTTP, address);
            conn = url.openConnection(proxy);
        } else {
            conn = url.openConnection();
        }

        HttpURLConnection connection = (HttpURLConnection) conn;
        conn.setRequestProperty("PircBotUser-Agent", StaticStrings.HTTP_USER_AGENT);
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);

        PrintWriter out = new PrintWriter(connection.getOutputStream());

        // encode parameters and concate
        StringBuilder sb = new StringBuilder();
        for (String param : params) {
            sb.append(URLEncoder.encode(param, encoding));
//      sb.append(param);
            sb.append("&");
        }
        out.println(sb.toString());
        out.close();


        String headerEncoding;
        if (encoding != null) {
            headerEncoding = encoding;
        } else {
            headerEncoding = getEncodingFromHeaders(connection);
        }

        InputStream in = connection.getInputStream();
        InputStreamReader isr = new InputStreamReader(in, headerEncoding);

        BufferedReader br =
                new BufferedReader(isr);

        htmlBuffer = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            htmlBuffer.append(line);
        }
        in.close();

    }

    /**
     * @return the String containing returned HTML-page from the URL
     */
    public String getHtmlBuffer() {
        return htmlBuffer.toString();
    }

    public String getEncodingFromHeaders(URLConnection conn) {
        String encoding = "utf-8";
        try {
            // Create a URLConnection object for a URL
//      URL url = new URL(urlStr);
//      URLConnection conn = url.openConnection();

            // List all the response headers from the server.
            // Note: The first call to getHeaderFieldKey() will implicit send
            // the HTTP request to the server.
            for (int i = 0; ; i++) {
                String headerName = conn.getHeaderFieldKey(i);
                String headerValue = conn.getHeaderField(i);
//        System.out.println(headerName + " = " + headerValue);
                if (headerName == null && headerValue == null) {
                    // No more headers
                    break;
                }
                if (headerName != null && headerName.equalsIgnoreCase("Content-Type")) {
                    String val = headerValue.toLowerCase();
                    if (val.matches(".*8859.*")) {
                        encoding = "8859_1";
                    } else {
                        encoding = "UTF-8";
                    }
                }
            }
        } catch (Exception e) {
            //
        }

        return encoding;
    }

}
