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
import com.google.gson.JsonSyntaxException;
import com.ringosham.translationmod.client.types.Language;
import com.ringosham.translationmod.client.types.RequestResult;
import com.ringosham.translationmod.client.types.google.TranslateError;
import com.ringosham.translationmod.client.types.google.TranslateResponse;
import com.ringosham.translationmod.common.ConfigManager;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class GooglePaidClient extends RESTClient {
    //This is the Google cloud translation API service
    //Rather over complicating things by logging in via OAuth/gcloud, we will just use the API key option to access the API
    //Logging in via gcloud requires the gcloud library. It's too big and unnecessary for a mod this small.
    //While OAuth is definitely more secure, since users are likely to create their own "project" in Google cloud console,
    //it's easier to just use an API key and it's more portable and sharable.
    private static boolean disable = false;

    public GooglePaidClient() {
        super("https://translation.googleapis.com/language/translate/v2");
    }

    public static void setDisable() {
        disable = true;
    }

    public static boolean getDisable() {
        return disable;
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
        //The query supports multiline using an array, however we only have one line to text to translate.
        queryParam.put("q", encodedMessage);
        //Query parameters
        queryParam.put("target", to.getGoogleCode());
        //Interpret text as... text. Apparently it defaults to HTML. What
        queryParam.put("format", "text");
        queryParam.put("key", ConfigManager.INSTANCE.getGoogleKey());
        //Skip the source language for auto detection
        if (from != LangManager.getInstance().getAutoLang()) {
            queryParam.put("source", from.getGoogleCode());
        }
        Response response = POST(queryParam, "application/json");
        String responseString = response.getEntity();
        Gson gson = new Gson();
        if (response.getResponseCode() == 200) {
            TranslateResponse transResponse = gson.fromJson(responseString, TranslateResponse.class);
            //Just get the first element. Since this is a single element query there should not be more than 1 entry in the array
            String translatedText = transResponse.getData().getTranslations()[0].getTranslatedText();
            Language sourceLang = LangManager.getInstance().findLanguageFromGoogle(transResponse.getData().getTranslations()[0].getDetectedSourceLanguage());
            if (sourceLang == null)
                sourceLang = from;
            return new RequestResult(200, translatedText, sourceLang, to);
        } else {
            //Internal errors
            if (response.getResponseCode() < 100) {
                return new RequestResult(response.getResponseCode(), response.getEntity(), null, null);
            }
            try {
                TranslateError error = gson.fromJson(responseString, TranslateError.class);
                return new RequestResult(error.getError().getCode(), error.getError().getMessage(), null, null);
            } catch (JsonSyntaxException e) {
                //Unknown response
                return new RequestResult(2, "Unknown response: " + responseString, null, null);
            }
        }
    }
}
