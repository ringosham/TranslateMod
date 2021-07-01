/*
 * Copyright (C) 2021 Ringosham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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

    @SuppressWarnings("SameParameterValue")
    protected Response sendRequest(String method, Map<String, String> queryParams, String contentType) {
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
            connection.setRequestProperty("Content-Type", contentType);
            connection.setRequestMethod(method);
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
            e.printStackTrace();
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
