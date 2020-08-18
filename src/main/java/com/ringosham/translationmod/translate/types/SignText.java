package com.ringosham.translationmod.translate.types;

import net.minecraft.util.math.BlockPos;

public class SignText {
    private String text;
    private BlockPos pos;

    public int getX() {
        return pos.getX();
    }

    public int getY() {
        return pos.getY();
    }

    public int getZ() {
        return pos.getZ();
    }

    public String getText() {
        return text;
    }

    public boolean sameSign(BlockPos pos) {
        return this.pos.getX() == pos.getX() && this.pos.getY() == pos.getY() && this.pos.getZ() == pos.getZ();
    }

    public void setSign(String text, BlockPos pos) {
        this.text = text;
        this.pos = pos;
    }
}
