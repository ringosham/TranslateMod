package com.ringosham.translationmod.client;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.ringosham.translationmod.client.types.Language;
import com.ringosham.translationmod.client.types.RequestResult;
import com.ringosham.translationmod.client.types.google.TranslateError;
import com.ringosham.translationmod.client.types.google.TranslateResponse;
import com.ringosham.translationmod.common.ConfigManager;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class GooglePaidClient {
    //This is the Google cloud translation API service
    //Rather over complicating things by logging in via OAuth/gcloud, we will just use the API key option to access the API
    //Logging in via gcloud requires the gcloud library. It's too big and unnecessary for a mod this small.
    //While OAuth is definitely more secure, since users are likely to create their own "project" in Google cloud console,
    //it's easier to just use an API key and it's more portable and sharable.
    private static final String baseUrl = "https://translation.googleapis.com/language/translate/v2";
    private static boolean disable = false;

    public static void setDisable() {
        disable = true;
    }

    public static boolean getDisable() {
        return disable;
    }

    public RequestResult translateAuto(String message, Language to) {
        return translate(message, LangManager.getInstance().getAutoLang(), to);
    }

    public RequestResult translate(String message, Language from, Language to) {
        HttpClient client = HttpClientBuilder.create().build();
        RequestBuilder builder = RequestBuilder.post();
        builder.setUri(baseUrl);
        //The query supports multiline using an array, however we only have one line to text to translate.
        //Query parameters
        builder.addParameter("q", message);
        builder.addParameter("target", to.getGoogleCode());
        //Interpret text as... text. Apparently it defaults to HTML, what
        builder.addParameter("format", "text");
        builder.addParameter("key", ConfigManager.config.userKey.get());
        //Skip the source language for auto detection
        if (from != LangManager.getInstance().getAutoLang()) {
            builder.addParameter("source", from.getGoogleCode());
        }
        HttpUriRequest request = builder.build();
        try {
            HttpResponse response = client.execute(request);
            InputStream in = response.getEntity().getContent();
            String responseString = IOUtils.toString(in, StandardCharsets.UTF_8);
            Gson gson = new Gson();
            if (response.getStatusLine().getStatusCode() == 200) {
                TranslateResponse transResponse = gson.fromJson(responseString, TranslateResponse.class);
                //Just get the first element. Since this is a single element query there should not be more than 1 entry in the array
                String translatedText = transResponse.getData().getTranslations()[0].getTranslatedText();
                Language sourceLang = LangManager.getInstance().findLanguageFromGoogle(transResponse.getData().getTranslations()[0].getDetectedSourceLanguage());
                if (sourceLang == null)
                    sourceLang = from;
                return new RequestResult(200, translatedText, sourceLang, to);
            } else {
                try {
                    TranslateError error = gson.fromJson(responseString, TranslateError.class);
                    return new RequestResult(error.getError().getCode(), error.getError().getMessage(), null, null);
                } catch (JsonSyntaxException e) {
                    //Unknown response
                    return new RequestResult(2, "Unknown response: " + responseString, null, null);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return new RequestResult(1, "Connection error", null, null);
        }
    }
}
