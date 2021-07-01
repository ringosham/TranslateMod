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
import com.google.gson.JsonObject;
import com.ringosham.translationmod.client.types.Language;
import com.ringosham.translationmod.client.types.RequestResult;
import com.ringosham.translationmod.client.types.baidu.TranslateError;
import com.ringosham.translationmod.client.types.baidu.TranslateSuccess;
import com.ringosham.translationmod.common.ConfigManager;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class BaiduClient extends RESTClient {

    private static final char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    public BaiduClient() {
        super("https://fanyi-api.baidu.com/api/trans/vip/translate");
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
        //Query message
        queryParam.put("q", encodedMessage);
        //Language codes from Baidu does not follow International standards at all
        //From language
        queryParam.put("to", to.getBaiduCode());
        //To language
        queryParam.put("from", from.getBaiduCode());
        //App ID
        queryParam.put("appid", ConfigManager.INSTANCE.getBaiduAppId());
        //Salt
        String salt = String.valueOf(System.currentTimeMillis());
        queryParam.put("salt", salt);
        //Signature
        //Must use unencoded message for signature
        queryParam.put("sign", sign(ConfigManager.INSTANCE.getBaiduAppId(), message, salt, ConfigManager.INSTANCE.getBaiduKey()));
        Response response = sendRequest("POST", queryParam, "application/x-www-form-urlencoded");
        if (response.getResponseCode() == 200) {
            //Baidu does not follow standard REST response codes at all. It's 200 regardless of success or failure
            //This is utterly retarded
            //wrrrrryyyyyyyyyy
            Gson gson = new Gson();
            JsonObject parse = gson.fromJson(response.getEntity(), JsonObject.class);
            if (!parse.has("error_code")) {
                //Success
                TranslateSuccess success = gson.fromJson(response.getEntity(), TranslateSuccess.class);
                Language sourceLang = LangManager.getInstance().findLanguageFromBaidu(success.getFrom());
                return new RequestResult(response.getResponseCode(), success.getTrans_result()[0].getDstDecoded(), sourceLang, to);
            } else {
                //Error
                TranslateError error = gson.fromJson(response.getEntity(), TranslateError.class);
                return new RequestResult(error.getError_code(), null, null, null);
            }
        } else {
            //Most likely some arbitrary internal server error
            return new RequestResult(response.getResponseCode(), null, null, null);
        }
    }

    private String sign(String appid, String q, String salt, String key) {
        //Credits to Baidu's example
        String rawSignString = appid + q + salt + key;
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] rawSignBytes = rawSignString.getBytes(StandardCharsets.UTF_8);
            byte[] signBytes = digest.digest(rawSignBytes);
            //Turn it to a hex string
            char[] signChars = new char[signBytes.length * 2];
            int index = 0;
            for (byte b : signBytes) {
                //Turn MSB to hex
                //Bytes in Java are signed. Unsigned shift by 4 to get MSB
                //(Need to AND 0xf because the shift converted the byte back to int)
                signChars[index++] = hexDigits[b >>> 4 & 0xf];
                //Turn LSB to hex
                //Bitwise AND to eliminate MSB
                signChars[index++] = hexDigits[b & 0xf];
            }
            return new String(signChars);
        } catch (NoSuchAlgorithmException ignored) {
        }
        return null;
    }
}
