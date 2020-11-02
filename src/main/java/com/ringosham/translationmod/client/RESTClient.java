package com.ringosham.translationmod.client;

import com.ringosham.translationmod.client.types.Language;
import com.ringosham.translationmod.client.types.RequestResult;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * An extremely simple class to perform a REST call in pure Java
 * Due to Apache's HttpClient not able to decode the response entity correctly, the rest client is now written in pure Java.
 * See Github issue #1
 */
public abstract class RESTClient {

    protected final String baseUrl;

    protected RESTClient(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public RequestResult translateAuto(String message, Language to) {
        return translate(message, LangManager.getInstance().getAutoLang(), to);
    }

    public abstract RequestResult translate(String message, Language from, Language to);


    protected Response POST(Map<String, String> queryParams) {
        StringBuilder requestUrl = new StringBuilder(baseUrl);
        boolean firstParam = true;
        for (String key : queryParams.keySet()) {
            if (firstParam) {
                requestUrl.append("?");
                firstParam = false;
            } else {
                requestUrl.append("&");
            }
            requestUrl.append(key).append("=").append(queryParams.get(key));
        }
        HttpsURLConnection connection;
        try {
            URL request = new URL(requestUrl.toString());
            connection = (HttpsURLConnection) request.openConnection();
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);
            connection.setConnectTimeout(5000);
            connection.connect();
            if (connection.getResponseCode() == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
                StringBuilder responseString = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    responseString.append(line);
                }
                return new Response(connection.getResponseCode(), responseString.toString());
            } else {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getErrorStream(), StandardCharsets.UTF_8));
                StringBuilder responseString = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    responseString.append(line);
                }
                return new Response(connection.getResponseCode(), responseString.toString());
            }
        } catch (MalformedURLException ignored) {
            return null;
        } catch (IOException e) {
            return new Response(1, "Failed to connect to server");
        }
    }

    public static class Response {
        private final int responseCode;
        private final String entity;

        public Response(int responseCode, String entity) {
            this.responseCode = responseCode;
            this.entity = entity;
        }

        public int getResponseCode() {
            return responseCode;
        }

        public String getEntity() {
            return entity;
        }
    }
}
