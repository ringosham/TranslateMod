package com.ringosham.translationmod.common;

import net.minecraftforge.fml.ModList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Log {
    public static final Logger logger;

    static {
        @SuppressWarnings("OptionalGetWithoutIsPresent")
        String modName = ModList.get().getModContainerById("translationmod").get().getModInfo().getDisplayName();
        logger = LogManager.getLogger(modName);
    }
}
