package com.ringosham.translationmod.client;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import com.ringosham.translationmod.client.models.Language;
import com.ringosham.translationmod.client.models.RequestResult;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.InputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;

public class YandexClient {
    private static final String baseUrl = "https://translate.yandex.net/api/v1.5/tr.json/";

    private String getDetection() {
        return baseUrl + "detect";
    }

    private String getTranslate() {
        return baseUrl + "translate";
    }

    public RequestResult detect(String key, String message) {
        HttpClient client = HttpClientBuilder.create().build();
        HttpUriRequest request = RequestBuilder.get().setUri(getDetection())
                .setHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType())
                .addParameter("key", key)
                .addParameter("text", message)
                .build();
        try {
            HttpResponse response = client.execute(request);
            InputStream in = response.getEntity().getContent();
            String responseString = IOUtils.toString(in, StandardCharsets.UTF_8);
            Gson gson = new Gson();
            JsonReader reader = new JsonReader(new StringReader(responseString));
            reader.setLenient(true);
            JsonObject json = gson.fromJson(reader, JsonObject.class);
            //Log.logger.info(json.toString());
            int code = json.get("code").getAsInt();
            String lang = null;
            String error = null;
            if (json.has("lang"))
                lang = json.get("lang").getAsString();
            else
                error = json.get("message").getAsString();
            if (lang != null)
                return new RequestResult(code, lang);
            else
                return new RequestResult(code, error);
        } catch (Exception e) {
            e.printStackTrace();
            return new RequestResult(1, "Connection error");
        }
    }

    public boolean testKey(String key) {
        RequestResult result = detect(key, "Deceive your other self. Deceive the world. That is what you must do to reach Steins Gate.");
        return result.getCode() == 200;
    }

    public RequestResult translateAuto(String key, String message, Language lang) {
        return translate(key, message, null, lang);
    }

    public RequestResult translate(String key, String message, Language from, Language to) {
        HttpClient client = HttpClientBuilder.create().build();
        HttpUriRequest request = RequestBuilder.get().setUri(getTranslate())
                .setHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType())
                .addParameter("key", key)
                .addParameter("text", message)
                .addParameter("lang", (from == null ? "" : from.getYandexCode() + "-") + to.getYandexCode())
                .build();
        try {
            HttpResponse response = client.execute(request);
            InputStream in = response.getEntity().getContent();
            String responseString = IOUtils.toString(in, StandardCharsets.UTF_8);
            Gson gson = new Gson();
            JsonReader reader = new JsonReader(new StringReader(responseString));
            reader.setLenient(true);
            JsonObject json = gson.fromJson(reader, JsonObject.class);
            int code = json.get("code").getAsInt();
            String text = null;
            String error = null;
            if (json.has("text"))
                text = (json.get("text").getAsJsonArray()).get(0).getAsString();
            else
                error = json.get("message").getAsString();
            if (text != null)
                return new RequestResult(code, text);
            else
                return new RequestResult(code, error);
        } catch (Exception e) {
            e.printStackTrace();
            return new RequestResult(1, "Connection error");
        }
    }
}
