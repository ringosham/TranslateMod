package com.ringosham.translationmod.events;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.lwjgl.glfw.GLFW;

public class KeyBind {
    static KeyBinding translateKey;

    public static void keyInit() {
        translateKey = new KeyBinding("Translator menu", GLFW.GLFW_KEY_Y, "key.categories.multiplayer");
        ClientRegistry.registerKeyBinding(translateKey);
    }
}
