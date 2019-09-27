package com.ringosham.translatemod.events;

import com.ringosham.translatemod.client.KeyManager;
import com.ringosham.translatemod.common.ChatUtil;
import com.ringosham.translatemod.common.ConfigManager;
import com.ringosham.translatemod.common.Log;
import com.ringosham.translatemod.gui.TranslateGui;
import com.ringosham.translatemod.translate.SignTranslate;
import com.ringosham.translatemod.translate.Translator;
import com.ringosham.translatemod.translate.model.SignText;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

import java.util.Objects;

public class Handler {
    private static Thread readSign;
    private SignText lastSign;
    private boolean hintShown = false;
    private int ticks = 0;

    @SubscribeEvent
    public void onGuiOpen(GuiOpenEvent event) {
        if (event.getGui() == null) {
            Keyboard.enableRepeatEvents(false);
        }
    }

    @SubscribeEvent
    public void chatReceived(ClientChatReceivedEvent event) {
        ITextComponent eventMessage = event.getMessage();
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
                ChatUtil.printChatMessage(true, "You are currently offline. To enable translations, please check your network settings and restart the game", TextFormatting.RED);
                return;
            } else {
                ChatUtil.printChatMessage(true, "Press [" + TextFormatting.AQUA + Keyboard.getKeyName(KeyBind.translateKey.getKeyCode()) + TextFormatting.WHITE + "] for translation settings", TextFormatting.WHITE);
            }
            if (KeyManager.getInstance().isKeyUsedUp()) {
                ChatUtil.printChatMessage(true, "All translation keys have been used up for today. The mod will not function without a translation key", TextFormatting.RED);
                ChatUtil.printChatMessage(true, "You can go to the mod settings -> User key. You can add your own translation key there.", TextFormatting.RED);
            }
            if (ConfigManager.INSTANCE.getRegexList().size() == 0) {
                Log.logger.warn("No chat regex in the configurations");
                ChatUtil.printChatMessage(true, "The mod needs chat regex to function. Check the mod options to add one", TextFormatting.RED);
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
    public void onKeybind(InputEvent.KeyInputEvent event) {
        if (!KeyManager.getInstance().isOffline() && KeyBind.translateKey.isPressed())
            Minecraft.getMinecraft().displayGuiScreen(new TranslateGui());
    }

    private void processSign(World world) {
        RayTraceResult mouseOver = Minecraft.getMinecraft().objectMouseOver;
        if (mouseOver == null)
            return;
        else if (mouseOver.typeOfHit != RayTraceResult.Type.BLOCK) {
            lastSign = null;
            ticks = 0;
            return;
        }
        BlockPos blockPos = mouseOver.getBlockPos();
        int x = blockPos.getX();
        int y = blockPos.getY();
        int z = blockPos.getZ();
        //Ignore air tiles
        if (world.getBlockState(blockPos).getBlock() == Blocks.AIR)
            return;
        //Wall signs and standing signs
        if (world.getBlockState(blockPos).getBlock() == Blocks.STANDING_SIGN || world.getBlockState(blockPos).getBlock() == Blocks.WALL_SIGN) {
            //Ensure the player is staring at the same sign
            if (lastSign != null && lastSign.getText() != null) {
                if (lastSign.sameSign(blockPos)) {
                    ticks++;
                    //Count number of ticks the player is staring
                    //Assuming 20 TPS
                    if (ticks >= 20)
                        if (readSign != null && readSign.getState() == Thread.State.NEW)
                            readSign.start();
                } else
                    readSign = getSignThread(world, blockPos);
            } else
                readSign = getSignThread(world, blockPos);
        } else {
            lastSign = null;
            ticks = 0;
        }
    }

    private SignTranslate getSignThread(World world, BlockPos pos) {
        StringBuilder text = new StringBuilder();
        //Four lines of text in signs
        for (int i = 0; i < 4; i++) {
            ITextComponent line = ((TileEntitySign) Objects.requireNonNull(world.getTileEntity(pos))).signText[i];
            //Combine each line of the sign with spaces.
            //Due to differences between languages, this may break asian languages. (Words don't separate with spaces)
            text.append(" ").append(line);
        }
        text = new StringBuilder(text.toString().replaceAll("§(.)", ""));
        if (text.length() == 0)
            return null;
        lastSign = new SignText();
        lastSign.setSign(text.toString(), pos);
        return new SignTranslate(text.toString(), pos);
    }
}
