package com.ringosham.translationmod.client;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.ringosham.translationmod.common.ConfigManager;
import com.ringosham.translationmod.common.Log;
import net.minecraft.client.Minecraft;
import net.minecraft.crash.CrashReport;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.codec.binary.Base64;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class KeyManager {

    private static final ResourceLocation keyLocation = new ResourceLocation("translationmod", "keys.json");
    private static KeyManager instance;
    private String currentKey;
    private int currentKeyIndex = 0;
    private boolean keyUsedUp = false;
    private boolean offline = false;
    private boolean rotating = false;
    private List<String> keys = new ArrayList<>();

    private KeyManager() {
    }

    public static KeyManager getInstance() {
        //Does not need to be thread safe as it only runs once only
        if (instance == null) {
            instance = new KeyManager();
            try {
                instance.init();
            } catch (IOException e) {
                Log.logger.fatal("Cannot locate/decode key file!");
                Minecraft.getMinecraft().displayCrashReport(new CrashReport("Failed to load key file", e));
            }
        }
        return instance;
    }

    public String getCurrentKey() {
        return currentKey;
    }

    //For personal translation keys. One key should be enough for one player.
    public void setCurrentKey(String currentKey) {
        this.currentKey = currentKey;
    }

    public boolean isKeyUsedUp() {
        return keyUsedUp;
    }

    //Rotates the key list until none of the keys work.
    //Only one thread can run this method at a time.
    //Must be synchronized
    public synchronized boolean rotateKey() {
        rotating = true;
        int count = 0;
        YandexClient client = new YandexClient();
        //Test the player's own key first
        if (!ConfigManager.INSTANCE.getUserKey().isEmpty()) {
            if (client.testKey(ConfigManager.INSTANCE.getUserKey()))
                currentKey = ConfigManager.INSTANCE.getUserKey();
            return true;
        }
        //Iterate through the entire list
        while (count < keys.size()) {
            count++;
            if (currentKeyIndex == keys.size())
                currentKeyIndex = 0;
            String newKey = keys.get(currentKeyIndex);
            if (client.testKey(newKey)) {
                currentKey = newKey;
                rotating = false;
                return true;
            }
            currentKeyIndex++;
        }
        currentKey = null;
        keyUsedUp = true;
        rotating = false;
        return false;
    }

    private void init() throws IOException {
        try (Socket socket = new Socket()) {
            //A connection to the DNS should be enough to test if host is online
            InetSocketAddress address = new InetSocketAddress("1.1.1.1", 80);
            socket.connect(address);
        } catch (IOException e) {
            offline = true;
            Log.logger.fatal("Host is offline! Disable all mod functions until game restarts");
            return;
        }
        //Read the keys and store to memory
        InputStream in = Minecraft.getMinecraft().getResourceManager().getResource(keyLocation).getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        Gson gson = new Gson();
        JsonObject json = gson.fromJson(reader, JsonObject.class);
        String encodedKeys = json.get("keys").toString();
        //Why not use the Base64 from java.util? Because this is Java 7.
        String keyString = new String(Base64.decodeBase64(encodedKeys), StandardCharsets.UTF_8);
        keys = Arrays.asList(keyString.split(","));
        //Log.logger.info(keys);
        //Select a key to use
        boolean result = rotateKey();
        if (!result)
            Log.logger.warn("All translation keys have been used up for the day. The mod may be unusable");
    }

    //Needed for checking if a rotation is in progress so that the mod won't spam the server
    public boolean isRotating() {
        return rotating;
    }

    public boolean isOffline() {
        return offline;
    }
}
