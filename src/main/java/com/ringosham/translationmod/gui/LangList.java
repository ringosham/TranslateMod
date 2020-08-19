package com.ringosham.translationmod.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.ringosham.translationmod.client.LangManager;
import com.ringosham.translationmod.client.types.Language;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.list.ExtendedList;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.MathHelper;

import java.util.Comparator;
import java.util.List;

public class LangList extends ExtendedList<LangList.LangEntry> {
    private final List<Language> langList;
    private final FontRenderer font;

    {
        langList = LangManager.getInstance().getAllLanguages();
        //Exclude auto
        langList.remove(LangManager.getInstance().getAutoLang());
        //Sort alphabetically.
        langList.sort(Comparator.comparing(Language::getName));
    }

    public LangList(Minecraft client, FontRenderer font, int width, int height, int top, int bottom, int entryHeight) {
        super(client, width, height, top, bottom, entryHeight);
        this.font = font;
        for (Language lang : langList)
            this.addEntry(new LangEntry(lang));
    }

    @Override
    protected int getScrollbarPosition() {
        return getRight();
    }

    @Override
    public int getRowWidth() {
        return this.width;
    }

    @Override
    protected boolean isFocused() {
        return true;
    }

    //The dirt background does not render correctly and it covers UI elements, needs to be disabled
    //Had to extract the entire rendering method as by default it renders the dirt background and there is no way to override it. This is so dumb.
    @SuppressWarnings({"NullableProblems", "deprecation"})
    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        int i = this.getScrollbarPosition();
        int j = i + 6;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        this.minecraft.getTextureManager().bindTexture(AbstractGui.BACKGROUND_LOCATION);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        float f = 32.0F;
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        bufferbuilder.pos(this.x0, this.y1, 0.0D).tex((float) this.x0 / 32.0F, (float) (this.y1 + (int) this.getScrollAmount()) / 32.0F).color(32, 32, 32, 255).endVertex();
        bufferbuilder.pos(this.x1, this.y1, 0.0D).tex((float) this.x1 / 32.0F, (float) (this.y1 + (int) this.getScrollAmount()) / 32.0F).color(32, 32, 32, 255).endVertex();
        bufferbuilder.pos(this.x1, this.y0, 0.0D).tex((float) this.x1 / 32.0F, (float) (this.y0 + (int) this.getScrollAmount()) / 32.0F).color(32, 32, 32, 255).endVertex();
        bufferbuilder.pos(this.x0, this.y0, 0.0D).tex((float) this.x0 / 32.0F, (float) (this.y0 + (int) this.getScrollAmount()) / 32.0F).color(32, 32, 32, 255).endVertex();
        tessellator.draw();
        int k = this.getRowLeft();
        int l = this.y0 + 4 - (int) this.getScrollAmount();

        this.renderList(matrixStack, k, l, mouseX, mouseY, partialTicks);

        RenderSystem.depthFunc(515);
        RenderSystem.disableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE);
        RenderSystem.disableAlphaTest();
        RenderSystem.shadeModel(7425);
        RenderSystem.disableTexture();
        int j1 = 4;
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        bufferbuilder.pos(this.x0, this.y0 + 4, 0.0D).tex(0.0F, 1.0F).color(0, 0, 0, 0).endVertex();
        bufferbuilder.pos(this.x1, this.y0 + 4, 0.0D).tex(1.0F, 1.0F).color(0, 0, 0, 0).endVertex();
        bufferbuilder.pos(this.x1, this.y0, 0.0D).tex(1.0F, 0.0F).color(0, 0, 0, 255).endVertex();
        bufferbuilder.pos(this.x0, this.y0, 0.0D).tex(0.0F, 0.0F).color(0, 0, 0, 255).endVertex();
        bufferbuilder.pos(this.x0, this.y1, 0.0D).tex(0.0F, 1.0F).color(0, 0, 0, 255).endVertex();
        bufferbuilder.pos(this.x1, this.y1, 0.0D).tex(1.0F, 1.0F).color(0, 0, 0, 255).endVertex();
        bufferbuilder.pos(this.x1, this.y1 - 4, 0.0D).tex(1.0F, 0.0F).color(0, 0, 0, 0).endVertex();
        bufferbuilder.pos(this.x0, this.y1 - 4, 0.0D).tex(0.0F, 0.0F).color(0, 0, 0, 0).endVertex();
        tessellator.draw();
        int k1 = Math.max(0, this.getMaxPosition() - (this.y1 - this.y0 - 4));
        if (k1 > 0) {
            int l1 = (int) ((float) ((this.y1 - this.y0) * (this.y1 - this.y0)) / (float) this.getMaxPosition());
            l1 = MathHelper.clamp(l1, 32, this.y1 - this.y0 - 8);
            int i2 = (int) this.getScrollAmount() * (this.y1 - this.y0 - l1) / k1 + this.y0;
            if (i2 < this.y0) {
                i2 = this.y0;
            }

            bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
            bufferbuilder.pos(i, this.y1, 0.0D).tex(0.0F, 1.0F).color(0, 0, 0, 255).endVertex();
            bufferbuilder.pos(j, this.y1, 0.0D).tex(1.0F, 1.0F).color(0, 0, 0, 255).endVertex();
            bufferbuilder.pos(j, this.y0, 0.0D).tex(1.0F, 0.0F).color(0, 0, 0, 255).endVertex();
            bufferbuilder.pos(i, this.y0, 0.0D).tex(0.0F, 0.0F).color(0, 0, 0, 255).endVertex();
            bufferbuilder.pos(i, i2 + l1, 0.0D).tex(0.0F, 1.0F).color(128, 128, 128, 255).endVertex();
            bufferbuilder.pos(j, i2 + l1, 0.0D).tex(1.0F, 1.0F).color(128, 128, 128, 255).endVertex();
            bufferbuilder.pos(j, i2, 0.0D).tex(1.0F, 0.0F).color(128, 128, 128, 255).endVertex();
            bufferbuilder.pos(i, i2, 0.0D).tex(0.0F, 0.0F).color(128, 128, 128, 255).endVertex();
            bufferbuilder.pos(i, i2 + l1 - 1, 0.0D).tex(0.0F, 1.0F).color(192, 192, 192, 255).endVertex();
            bufferbuilder.pos(j - 1, i2 + l1 - 1, 0.0D).tex(1.0F, 1.0F).color(192, 192, 192, 255).endVertex();
            bufferbuilder.pos(j - 1, i2, 0.0D).tex(1.0F, 0.0F).color(192, 192, 192, 255).endVertex();
            bufferbuilder.pos(i, i2, 0.0D).tex(0.0F, 0.0F).color(192, 192, 192, 255).endVertex();
            tessellator.draw();
        }

        this.renderDecorations(matrixStack, mouseX, mouseY);
        RenderSystem.enableTexture();
        RenderSystem.shadeModel(7424);
        RenderSystem.enableAlphaTest();
        RenderSystem.disableBlend();
    }

    public class LangEntry extends ExtendedList.AbstractListEntry<LangEntry> {
        private final Language lang;
        private final String langName;

        public LangEntry(Language lang) {
            this.lang = lang;
            if (lang.getNameUnicode() != null)
                this.langName = lang.getName() + " (" + lang.getNameUnicode() + ")";
            else
                this.langName = lang.getName();
        }

        @SuppressWarnings("NullableProblems")
        @Override
        public void render(MatrixStack stack, int entryIdx, int top, int left, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean p_194999_5_, float partialTicks) {
            //font.setBidiFlag(true);
            if (top + 1 < getBottom() && getTop() < top + 1)
                drawString(stack, font, this.langName, left + 5, top + 1, 16777215);
        }

        //Undocumented parameter names are fun!
        @Override
        public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
            if (p_mouseClicked_5_ == 0) {
                LangList.this.setSelected(this);
                return true;
            } else {
                return false;
            }
        }

        public Language getLang() {
            return lang;
        }
    }
}
