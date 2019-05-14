package com.theoxao.commons.web;

import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by Dolphin on 2017/12/21
 */
@Slf4j
public class HttpClient {
    private static OkHttpClient CLIENT = new OkHttpClient.Builder().connectTimeout(20, TimeUnit.SECONDS).readTimeout(20, TimeUnit.SECONDS).build();

    /**
     * map hosts to local host, in order to serve external url requests by MockWebServer, only for unit test
     */
    public static void mapLocal(@SuppressWarnings("unused") String... hosts) {
        CLIENT = new OkHttpClient.Builder().dns(hostname -> Collections.singletonList(InetAddress.getLoopbackAddress())).build();
    }

    public static byte[] post(String url, MediaType mediaType, String body) throws IOException {
        RequestBody requestBody = RequestBody.create(mediaType, body);
        Request request = new Request.Builder().url(url).post(requestBody).build();
        Response response = CLIENT.newCall(request).execute();
        if (!response.isSuccessful()) {
            log.error("request failed, url:{}, mediaType: {}, body:{}", url, mediaType, body);
            throw new IOException("request failed!");
        }
        return response.body().bytes();
    }

    public static byte[] post(String url) throws IOException {
        RequestBody requestBody = RequestBody.create(MediaType.parse("text/xml"), "");
        Request request = new Request.Builder().url(url).post(requestBody).build();
        Response response = CLIENT.newCall(request).execute();
        if (!response.isSuccessful()) {
            log.error("request failed, url:{}", url);
            throw new IOException("request failed!");
        }
        return response.body().bytes();
    }

    public static byte[] post(String url, OkHttpClient client) throws IOException {
        RequestBody requestBody = RequestBody.create(MediaType.parse("text/xml"), "");
        Request request = new Request.Builder().url(url).post(requestBody).build();
        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) {
            log.error("request failed, url:{}", url);
            throw new IOException("request failed!");
        }
        return response.body().bytes();
    }

    public static byte[] get(String url) throws IOException {
        Request request = new Request.Builder().url(url).addHeader("Connection", "close").get().build();
        try (Response response = CLIENT.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                log.error("request failed, url:{}", url);
                throw new IOException("request failed!");
            }
            return response.body().bytes();
        } catch (Exception e) {
            e.printStackTrace();
            return new byte[0];
        }
    }

    public static byte[] get(String url, Map<String, String> params) throws IOException {
        String actualUrl = appendQueryParameters(url, params);
        Request request = new Request.Builder().url(actualUrl).get().build();
        Response response = CLIENT.newCall(request).execute();
        if (!response.isSuccessful()) {
            log.error("request failed, url:{}", actualUrl);
            throw new IOException("request failed!");
        }
        return response.body().bytes();
    }

    private static String appendQueryParameters(String url, Map<String, String> params) {
        if (params.isEmpty()) {
            return url;
        }
        try {

            String queryString = toQueryString(params);
            int anchorPos = url.indexOf('#');
            String main = url;
            boolean hasAnchor = false;
            if (anchorPos != -1) {
                main = url.substring(0, anchorPos);
                hasAnchor = true;
            }
            int markPos = main.indexOf('?');
            if (markPos == -1) {
                StringBuilder result = new StringBuilder();
                result.append(main).append('?').append(queryString);
                if (hasAnchor) {
                    result.append(url.substring(anchorPos));
                }
                return result.toString();
            } else {
                StringBuilder result = new StringBuilder();
                result.append(main);
                char lastChar = main.charAt(main.length() - 1);
                if (lastChar != '&' && lastChar != '?') {
                    result.append('&');
                }
                result.append(queryString);
                if (hasAnchor) {
                    result.append(url.substring(anchorPos));
                }
                return result.toString();
            }
        } catch (Exception e) {
            throw new RuntimeException("");
        }
    }

    private static String toQueryString(Map<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            result.append(entry.getKey()).append('=').append(URLEncoder.encode(entry.getValue(), "UTF-8")).append('&');
        }
        if (result.length() > 0) {
            result.setLength(result.length() - 1);
        }
        return result.toString();
    }
}
