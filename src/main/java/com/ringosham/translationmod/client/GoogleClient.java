package com.ringosham.translationmod.client;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
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

public class GoogleClient {
    //Google translate is a paid service. This is the secret free API for use of the web Google translate
    private static final String baseUrl = "https://translate.googleapis.com/translate_a/single";
    private static boolean accessDenied = false;

    public static boolean isAccessDenied() {
        return accessDenied;
    }

    public RequestResult translate(String message, Language from, Language to) {
        HttpClient client = HttpClientBuilder.create().build();
        //Necessary query parameters to trick Google translate
        HttpUriRequest request = RequestBuilder.get().setUri(baseUrl)
                .setHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType())
                .addParameter("client", "gtx")
                .addParameter("sl", from.getGoogleCode())
                .addParameter("tl", to.getGoogleCode())
                .addParameter("dt", "t")
                .addParameter("q", message)
                .build();
        try {
            HttpResponse response = client.execute(request);
            if (response.getStatusLine().getStatusCode() != 200) {
                accessDenied = true;
                return new RequestResult(429, "Access to Google Translate denied");
            }
            InputStream in = response.getEntity().getContent();
            String responseString = IOUtils.toString(in, StandardCharsets.UTF_8);
            //This secret API is specifically made for Google translate. So the response contains lots of useless information.
            //Each sentence translated is divided into separate JSON arrays.
            Gson gson = new Gson();
            //In case the server returns something dumb.
            JsonReader reader = new JsonReader(new StringReader(responseString));
            reader.setLenient(true);
            JsonArray json = gson.fromJson(reader, JsonArray.class);
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
