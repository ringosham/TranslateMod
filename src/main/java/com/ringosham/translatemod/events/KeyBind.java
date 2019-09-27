package com.ringosham.translatemod.events;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.lwjgl.input.Keyboard;

public class KeyBind {
    static KeyBinding translateKey;

    public static void keyInit() {
        translateKey = new KeyBinding("Translator menu", Keyboard.KEY_Y, "key.categories.multiplayer");
        ClientRegistry.registerKeyBinding(translateKey);
    }
}
