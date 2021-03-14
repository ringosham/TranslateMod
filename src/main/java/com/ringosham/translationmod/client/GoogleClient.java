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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.ringosham.translationmod.client.types.Language;
import com.ringosham.translationmod.client.types.RequestResult;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class GoogleClient extends RESTClient {
    //Google translate is a paid service. This is the secret free API for use of the web Google translate
    private static boolean accessDenied = false;

    public GoogleClient() {
        super("https://translate.googleapis.com/translate_a/single");
    }

    public static boolean isAccessDenied() {
        return accessDenied;
    }

    @Override
    public RequestResult translate(String message, Language from, Language to) {
        Map<String, String> queryParam = new HashMap<>();
        String encodedMessage = null;
        //Percent encode message
        try {
            encodedMessage = URLEncoder.encode(message, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException ignored) {
        }
        //Necessary query parameters to trick Google translate
        queryParam.put("client", "gtx");
        queryParam.put("sl", from.getGoogleCode());
        queryParam.put("tl", to.getGoogleCode());
        queryParam.put("dt", "t");
        queryParam.put("q", encodedMessage);
        try {
            Response response = POST(queryParam, "application/json");
            //Usually Google would just return 429 if they deny access, but just in case it gives any other HTTP error codes
            if (response.getResponseCode() != 200) {
                accessDenied = true;
                Thread timeout = new Timeout();
                timeout.start();
                return new RequestResult(429, "Access to Google Translate denied", null, null);
            }
            String responseString = response.getEntity();
            //This secret API is specifically made for Google translate. So the response contains lots of useless information.
            //Each sentence translated is divided into separate JSON arrays.
            Gson gson = new GsonBuilder().setLenient().create();
            JsonArray json = gson.fromJson(responseString, JsonArray.class);
            Language detectedSource = LangManager.getInstance().findLanguageFromGoogle(json.get(2).getAsString());
            JsonArray lines = json.get(0).getAsJsonArray();
            StringBuilder stringBuilder = new StringBuilder();
            for (JsonElement sentenceObj : lines) {
                JsonArray sentence = sentenceObj.getAsJsonArray();
                stringBuilder.append(sentence.get(0).getAsString());
                stringBuilder.append(" ");
            }
            return new RequestResult(200, stringBuilder.toString(), detectedSource, to);
        } catch (Exception e) {
            e.printStackTrace();
            return new RequestResult(1, "Connection error", null, null);
        }
    }

    //A timeout thread in case Google blocks user access to the hidden API
    //It is unknown how long Google blocks, so I'll assume 5 minutes
    private static class Timeout extends Thread {
        @Override
        public void run() {
            try {
                Thread.sleep(300000);
                accessDenied = false;
            } catch (InterruptedException ignored) {
            }
        }
    }
}
