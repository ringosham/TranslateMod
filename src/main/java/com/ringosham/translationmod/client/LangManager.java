package com.ringosham.translationmod.client;

import com.google.gson.Gson;
import com.ringosham.translationmod.TranslationMod;
import com.ringosham.translationmod.client.types.Language;
import net.minecraft.util.ResourceLocation;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LangManager {

    //Loads the language list from local json
    //Technically the cloud translate API provides an endpoint to tell what languages are available, but even that requires an API key and payment info.
    private static final ResourceLocation langLocation = new ResourceLocation(TranslationMod.MODID, "lang.json");
    private static LangManager instance;
    private static Language autoLang;
    private List<Language> languages = new ArrayList<>();

    private LangManager() {
    }

    public static LangManager getInstance() {
        //Does not need to be thread safe since this is only ran once.
        if (instance == null) {
            instance = new LangManager();
            //Read all languages and stores in memory
            InputStream in = LangManager.class.getResourceAsStream("/assets/translationmod/lang.json");
            //InputStream in = Minecraft.getInstance().getResourceManager().getResource(langLocation).getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
            Gson gson = new Gson();
            Language[] langArray = gson.fromJson(reader, Language[].class);
            instance.languages = Arrays.asList(langArray);
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

    public List<Language> getAllLanguages() {
        return new ArrayList<>(languages);
    }

    //Returns auto detect as a "language"
    public Language getAutoLang() {
        if (autoLang == null)
            autoLang = findLanguageFromGoogle("auto");
        return autoLang;
    }
}
