package com.ringosham.translatemod;

import com.ringosham.translatemod.client.KeyManager;
import com.ringosham.translatemod.client.LangManager;
import com.ringosham.translatemod.common.ConfigManager;
import com.ringosham.translatemod.common.Log;
import com.ringosham.translatemod.events.Handler;
import com.ringosham.translatemod.events.KeyBind;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.common.MinecraftForge;

/**
 * Real time translation project - All rights reserved
 *
 * @author Ringosham
 * @since 9/11/2015
 */
@Mod(modid = "translationmod", name = "%mod_name%", version = "%minecraft_version%")
public class TranslateMod {
    private Handler handler = new Handler();

    @EventHandler
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

    @EventHandler
    public void init(FMLInitializationEvent e) {
        if (!KeyManager.getInstance().isOffline())
            KeyBind.keyInit();
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent e) {
        FMLCommonHandler.instance().bus().register(handler);
        MinecraftForge.EVENT_BUS.register(handler);
        Log.logger.info("Event bus initialized");
    }
}
