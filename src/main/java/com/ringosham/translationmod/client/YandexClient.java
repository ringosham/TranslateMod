package com.ringosham.translationmod.client;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.ringosham.translationmod.client.models.Language;
import com.ringosham.translationmod.client.models.RequestResult;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

public class YandexClient {
    private static final String baseUrl = "https://translate.yandex.net/api/v1.5/tr.json/";
    private Client client = ClientBuilder.newClient();

    private WebTarget getDetection() {
        return client.target(baseUrl + "detect");
    }

    private WebTarget getTranslate() {
        return client.target(baseUrl + "translate");
    }

    public RequestResult detect(String key, String message) {
        WebTarget target = getDetection();
        Map<String, String> params = new HashMap<>();
        params.put("key", key);
        params.put("text", message);
        for (String queryKey : params.keySet())
            target = target.queryParam(queryKey, params.get(queryKey));
        Invocation.Builder builder = target.request(MediaType.APPLICATION_JSON);
        try {
            Response response = builder.get();
            Gson gson = new Gson();
            JsonObject json = gson.fromJson(response.readEntity(String.class), JsonObject.class);
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
        WebTarget target = getTranslate();
        Map<String, String> params = new HashMap<>();
        params.put("key", key);
        params.put("text", message);
        params.put("lang", (from == null ? "" : from.getYandexCode() + "-") + to.getYandexCode());
        for (String queryKey : params.keySet())
            target = target.queryParam(queryKey, params.get(queryKey));
        Invocation.Builder builder = target.request(MediaType.APPLICATION_JSON);
        try {
            Response response = builder.get();
            Gson gson = new Gson();
            JsonObject json = gson.fromJson(response.readEntity(String.class), JsonObject.class);
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
