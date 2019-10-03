package com.ringosham.translationmod;

import com.ringosham.translationmod.client.KeyManager;
import com.ringosham.translationmod.client.LangManager;
import com.ringosham.translationmod.common.ConfigManager;
import com.ringosham.translationmod.common.Log;
import com.ringosham.translationmod.events.Handler;
import com.ringosham.translationmod.events.KeyBind;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

/**
 * Real time translation project - All rights reserved
 *
 * @author Ringosham
 * @since 9/11/2015
 */
@Mod(modid = "translationmod", name = "%mod_name%", version = "%mod_version%")
public class TranslateMod {
    private Handler handler = new Handler();

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent e) {
        //Languages must be loaded first before the configuration
        //The config manager needs to convert the language strings to language objects
        Log.logger.info("Hi there! Getting translation API ready (1/2)");
        //Initialize instance.
        LangManager.getInstance();
        Log.logger.info("Loading configurations");
        ConfigManager.INSTANCE.init(e);
        Log.logger.info("Hi there! Getting translation API ready (2/2)");
        //Initialize instance
        KeyManager.getInstance();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent e) {
        if (!KeyManager.getInstance().isOffline())
            KeyBind.keyInit();
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent e) {
        MinecraftForge.EVENT_BUS.register(handler);
        Log.logger.info("Event bus initialized");
    }
}
