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

package com.ringosham.translationmod;

import com.ringosham.translationmod.client.LangManager;
import com.ringosham.translationmod.common.ConfigManager;
import com.ringosham.translationmod.common.Log;
import com.ringosham.translationmod.events.Handler;
import com.ringosham.translationmod.events.KeyBind;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.common.MinecraftForge;

/**
 * Real time translation project - Licensed under GPL v3
 *
 * @author Ringosham
 * @since 9/11/2015
 */
@Mod(modid = "translationmod", name = "%mod_name%", version = "%mod_version%")
public class TranslationMod {
    private Handler handler = new Handler();

    @EventHandler
    public void preInit(FMLPreInitializationEvent e) {
        //Languages must be loaded first before the configuration
        //The config manager needs to convert the language strings to language objects
        Log.logger.info("Hi there! Getting translation API ready");
        //Initialize instance.
        LangManager.getInstance();
        Log.logger.info("Loading configurations");
        ConfigManager.INSTANCE.init(e);
        Log.logger.info("Ready! :)");
    }

    @EventHandler
    public void init(FMLInitializationEvent e) {
        KeyBind.keyInit();
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent e) {
        FMLCommonHandler.instance().bus().register(handler);
        MinecraftForge.EVENT_BUS.register(handler);
        Log.logger.info("Event bus initialized");
    }
}
