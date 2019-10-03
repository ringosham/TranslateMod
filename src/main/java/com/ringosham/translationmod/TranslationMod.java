package com.ringosham.translationmod;

import com.ringosham.translationmod.client.KeyManager;
import com.ringosham.translationmod.client.LangManager;
import com.ringosham.translationmod.common.ConfigManager;
import com.ringosham.translationmod.common.Log;
import com.ringosham.translationmod.events.Handler;
import com.ringosham.translationmod.events.KeyBind;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

/**
 * Real time translation project - All rights reserved
 *
 * @author Ringosham
 * @since 9/11/2015
 */
@Mod(TranslationMod.MODID)
public class TranslationMod {
    public static final String MODID = "translationmod";

    public TranslationMod() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);
        //Languages must be loaded first before the configuration
        //The config manager needs to convert the language strings to language objects
        Log.logger.info("Hi there! Getting translation API ready (1/2)");
        //Initialize instance.
        LangManager.getInstance();
        Log.logger.info("Loading configurations");
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ConfigManager.configSpec);
        ConfigManager.validateConfig();
        Log.logger.info("Hi there! Getting translation API ready (2/2)");
        //Initialize instance
        KeyManager.getInstance();
    }

    private void setup(FMLCommonSetupEvent e) {
        Handler handler = new Handler();
        MinecraftForge.EVENT_BUS.register(handler);
    }

    private void clientSetup(FMLClientSetupEvent e) {
        if (!KeyManager.getInstance().isOffline())
            KeyBind.keyInit();
    }
}
