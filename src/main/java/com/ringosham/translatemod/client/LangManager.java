package com.ringosham.translatemod.client;

import com.google.gson.Gson;
import com.ringosham.translatemod.client.models.Language;
import com.ringosham.translatemod.common.Log;
import net.minecraft.client.Minecraft;
import net.minecraft.crash.CrashReport;
import net.minecraft.util.ResourceLocation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LangManager {

    private static final ResourceLocation langLocation = new ResourceLocation("translationmod", "lang.json");
    private static LangManager instance;
    private List<Language> languages = new ArrayList<>();

    private LangManager() {
    }

    public static LangManager getInstance() {
        //Does not need to be thread safe since this is only ran once.
        if (instance == null) {
            instance = new LangManager();
            try {
                //Read all languages and stores in memory
                InputStream in = Minecraft.getMinecraft().getResourceManager().getResource(langLocation).getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
                Gson gson = new Gson();
                Language[] langArray = gson.fromJson(reader, Language[].class);
                instance.languages = Arrays.asList(langArray);
            } catch (IOException e) {
                Log.logger.fatal("Cannot load language file!");
                Minecraft.getMinecraft().displayCrashReport(new CrashReport("Failed to load language file", e));
            }
        }
        return instance;
    }

    public Language findLanguageFromName(String name) {
        for (Language lang : languages)
            if (lang.getName().equals(name))
                return lang;
        return null;
    }

    public Language findLanguageFromGoogle(String googleCode) {
        for (Language lang : languages)
            if (lang.getGoogleCode().equals(googleCode))
                return lang;
        return null;
    }

    public Language findLanguageFromYandex(String yandexCode) {
        for (Language lang : languages)
            if (lang.getYandexCode().equals(yandexCode))
                return lang;
        return null;
    }

    public List<Language> getAllLanguages() {
        return new ArrayList<>(languages);
    }
}
