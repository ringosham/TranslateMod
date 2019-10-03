package com.ringosham.translationmod.events;

import com.ringosham.translationmod.client.KeyManager;
import com.ringosham.translationmod.common.ChatUtil;
import com.ringosham.translationmod.common.ConfigManager;
import com.ringosham.translationmod.common.Log;
import com.ringosham.translationmod.gui.TranslateGui;
import com.ringosham.translationmod.translate.SignTranslate;
import com.ringosham.translationmod.translate.Translator;
import com.ringosham.translationmod.translate.model.SignText;
import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent.KeyInputEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.GuiOpenEvent;
import org.lwjgl.input.Keyboard;

public class Handler {
    private static Thread readSign;
    private SignText lastSign;
    private boolean hintShown = false;
    private int ticks = 0;

    @SubscribeEvent
    public void onGuiOpen(GuiOpenEvent event) {
        if (event.gui == null) {
            Keyboard.enableRepeatEvents(false);
        }
    }

    @SubscribeEvent
    public void chatReceived(ClientChatReceivedEvent event) {
        IChatComponent eventMessage = event.message;
        String message = eventMessage.getUnformattedText().replaceAll("§(.)", "");
        if (KeyManager.getInstance().isKeyUsedUp())
            return;
        Thread translate = new Translator(message, null, ConfigManager.INSTANCE.getTargetLanguage());
        translate.start();
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.player == null)
            return;
        if (!hintShown) {
            hintShown = true;
            if (KeyManager.getInstance().isOffline()) {
                ChatUtil.printChatMessage(true, "You are currently offline. To enable translations, please check your network settings and restart the game", EnumChatFormatting.RED);
                return;
            } else {
                ChatUtil.printChatMessage(true, "Press [" + EnumChatFormatting.AQUA + Keyboard.getKeyName(KeyBind.translateKey.getKeyCode()) + EnumChatFormatting.WHITE + "] for translation settings", EnumChatFormatting.WHITE);
            }
            if (KeyManager.getInstance().isKeyUsedUp()) {
                ChatUtil.printChatMessage(true, "All translation keys have been used up for today. The mod will not function without a translation key", EnumChatFormatting.RED);
                ChatUtil.printChatMessage(true, "You can go to the mod settings -> User key. You can add your own translation key there.", EnumChatFormatting.RED);
            }
            if (ConfigManager.INSTANCE.getRegexList().size() == 0) {
                Log.logger.warn("No chat regex in the configurations");
                ChatUtil.printChatMessage(true, "The mod needs chat regex to function. Check the mod options to add one", EnumChatFormatting.RED);
            }
        }
    }

    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent event) {
        if (event.world == null)
            return;
        //Scan for signs
        if (ConfigManager.INSTANCE.isTranslateSign() && event.phase == TickEvent.Phase.END) {
            processSign(event.world);
        }
    }

    //If config is somehow changed through other means
    @SubscribeEvent
    public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        ConfigManager.INSTANCE.saveConfig();
    }

    @SubscribeEvent
    public void onKeybind(KeyInputEvent event) {
        if (!KeyManager.getInstance().isOffline() && KeyBind.translateKey.isPressed())
            Minecraft.getMinecraft().displayGuiScreen(new TranslateGui());
    }

    private void processSign(World world) {
        MovingObjectPosition mouseOver = Minecraft.getMinecraft().objectMouseOver;
        if (mouseOver == null)
            return;
        else if (mouseOver.typeOfHit != MovingObjectType.BLOCK) {
            lastSign = null;
            ticks = 0;
            return;
        }
        int x = mouseOver.blockX;
        int y = mouseOver.blockY;
        int z = mouseOver.blockZ;
        //Ignore air tiles
        if (world.getBlock(x, y, z) == Block.getBlockById(0))
            return;
        //Wall signs and standing signs
        if (world.getBlock(x, y, z) == Block.getBlockById(63) || world.getBlock(x, y, z) == Block.getBlockById(68)) {
            //Ensure the player is staring at the same sign
            if (lastSign != null && lastSign.getText() != null) {
                if (lastSign.sameSign(x, y, z)) {
                    ticks++;
                    //Count number of ticks the player is staring
                    //Assuming 20 TPS
                    if (ticks >= 20)
                        if (readSign != null && readSign.getState() == Thread.State.NEW)
                            readSign.start();
                } else
                    readSign = getSignThread(world, x, y, z);
            } else
                readSign = getSignThread(world, x, y, z);
        } else {
            lastSign = null;
            ticks = 0;
        }
    }

    private SignTranslate getSignThread(World world, int x, int y, int z) {
        StringBuilder text = new StringBuilder();
        //Four lines of text in signs
        for (int i = 0; i < 4; i++) {
            String line = ((TileEntitySign) world.getTileEntity(x, y, z)).signText[i];
            //Combine each line of the sign with spaces.
            //Due to differences between languages, this may break asian languages. (Words don't separate with spaces)
            text.append(" ").append(line);
        }
        text = new StringBuilder(text.toString().replaceAll("§(.)", ""));
        if (text.length() == 0)
            return null;
        lastSign = new SignText();
        lastSign.setSign(text.toString(), x, y, z);
        return new SignTranslate(text.toString(), x, y, z);
    }
}
