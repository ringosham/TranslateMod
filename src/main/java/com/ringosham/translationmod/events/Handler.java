package com.ringosham.translationmod.events;

import com.ringosham.translationmod.client.LangManager;
import com.ringosham.translationmod.common.ChatUtil;
import com.ringosham.translationmod.common.ConfigManager;
import com.ringosham.translationmod.common.Log;
import com.ringosham.translationmod.gui.TranslateGui;
import com.ringosham.translationmod.translate.SignTranslate;
import com.ringosham.translationmod.translate.Translator;
import com.ringosham.translationmod.translate.types.SignText;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.tileentity.SignTileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.config.ModConfig.Reloading;

import java.util.Objects;

public class Handler {
    private static Thread readSign;
    private SignText lastSign;
    private boolean hintShown = false;
    private int ticks = 0;
    private static final Block[] signBlocks = {
            Blocks.ACACIA_SIGN,
            Blocks.ACACIA_BUTTON,
            Blocks.BIRCH_SIGN,
            Blocks.BIRCH_WALL_SIGN,
            Blocks.DARK_OAK_SIGN,
            Blocks.DARK_OAK_WALL_SIGN,
            Blocks.JUNGLE_SIGN,
            Blocks.JUNGLE_WALL_SIGN,
            Blocks.OAK_SIGN,
            Blocks.OAK_WALL_SIGN,
            Blocks.SPRUCE_SIGN,
            Blocks.SPRUCE_WALL_SIGN
    };

    @SubscribeEvent
    public void onGuiOpen(GuiOpenEvent event) {
        if (event.getGui() == null) {
            Minecraft.getInstance().keyboardListener.enableRepeatEvents(false);
        }
    }

    @SubscribeEvent
    public void chatReceived(ClientChatReceivedEvent event) {
        ITextComponent eventMessage = event.getMessage();
        String message = eventMessage.getString().replaceAll("§(.)", "");
        Thread translate = new Translator(message, null, LangManager.getInstance().findLanguageFromName(ConfigManager.config.targetLanguage.get()));
        translate.start();
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.player == null)
            return;
        if (!hintShown) {
            hintShown = true;
            ChatUtil.printChatMessage(true, "Press [" + TextFormatting.AQUA + KeyBinding.getDisplayString(KeyBind.translateKey.getKeyDescription()).get().getUnformattedComponentText() + TextFormatting.WHITE + "] for translation settings", TextFormatting.WHITE);
            if (ConfigManager.config.regexList.get().size() == 0) {
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
        if (ConfigManager.config.translateSign.get() && event.phase == TickEvent.Phase.END) {
            processSign(event.world);
        }
    }

    //If config is somehow changed through other means
    @SubscribeEvent
    public void onConfigChanged(Reloading event) {
        ConfigManager.saveConfig();
    }

    @SubscribeEvent
    public void onKeybind(InputEvent.KeyInputEvent event) {
        if (KeyBind.translateKey.isPressed())
            Minecraft.getInstance().displayGuiScreen(new TranslateGui());
    }

    private void processSign(World world) {
        RayTraceResult mouseOver = Minecraft.getInstance().objectMouseOver;
        if (mouseOver == null)
            return;
        else if (mouseOver.getType() != RayTraceResult.Type.BLOCK) {
            lastSign = null;
            ticks = 0;
            return;
        }
        Vector3d vec = mouseOver.getHitVec();
        BlockPos blockPos = new BlockPos(vec);
        //Ignore air tiles
        if (world.getBlockState(blockPos).getBlock() == Blocks.AIR)
            return;
        //Wall signs and standing signs
        boolean isSign = false;
        for (Block signBlock : signBlocks) {
            if (world.getBlockState(blockPos).getBlock() == signBlock) {
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
                isSign = true;
            }
        }
        if (!isSign) {
            lastSign = null;
            ticks = 0;
        }
    }

    private SignTranslate getSignThread(World world, BlockPos pos) {
        StringBuilder text = new StringBuilder();
        //Four lines of text in signs
        for (int i = 0; i < 4; i++) {
            ITextComponent line = ((SignTileEntity) Objects.requireNonNull(world.getTileEntity(pos))).getText(i);
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
