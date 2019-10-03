package com.ringosham.translationmod.events;

import cpw.mods.fml.client.registry.ClientRegistry;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;

public class KeyBind {
    static KeyBinding translateKey;

    public static void keyInit() {
        translateKey = new KeyBinding("Translator menu", Keyboard.KEY_Y, "key.categories.multiplayer");
        ClientRegistry.registerKeyBinding(translateKey);
    }
}
