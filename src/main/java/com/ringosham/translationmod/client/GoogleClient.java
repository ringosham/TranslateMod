package com.ringosham.translationmod.client;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
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

public class GoogleClient {
    //Google translate is a paid service. This is the secret free API for use of the web Google translate
    private static final String baseUrl = "https://translate.googleapis.com/translate_a/single";
    private static boolean accessDenied = false;
    private Client client = ClientBuilder.newClient();

    public static boolean isAccessDenied() {
        return accessDenied;
    }

    public RequestResult translate(String message, Language from, Language to) {
        WebTarget target = client.target(baseUrl);
        Map<String, String> params = new HashMap<>();
        //Necessary query parameters to trick Google translate
        params.put("client", "gtx");
        params.put("sl", from.getGoogleCode());
        params.put("tl", to.getGoogleCode());
        params.put("dt", "t");
        params.put("q", message);
        for (String queryKey : params.keySet())
            target = target.queryParam(queryKey, params.get(queryKey));
        Invocation.Builder builder = target.request(MediaType.APPLICATION_JSON);
        try {
            Response response = builder.get();
            if (response.getStatus() != 200) {
                accessDenied = true;
                return new RequestResult(429, "Access to Google Translate denied");
            }
            //This secret API is specifically made for Google translate. So the response contains lots of useless information.
            //Each sentence translated is divided into separate JSON arrays.
            Gson gson = new Gson();
            JsonArray json = gson.fromJson(response.readEntity(String.class), JsonArray.class);
            JsonArray lines = json.get(0).getAsJsonArray();
            StringBuilder stringBuilder = new StringBuilder();
            for (JsonElement sentenceObj : lines) {
                JsonArray sentence = sentenceObj.getAsJsonArray();
                stringBuilder.append(sentence.get(0).getAsString());
                stringBuilder.append(" ");
            }
            return new RequestResult(200, stringBuilder.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return new RequestResult(1, "Connection error");
        }
    }
}
