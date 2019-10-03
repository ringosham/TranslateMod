package com.ringosham.translationmod.translate.model;

public class SignText {
    private String text;
    private int x;
    private int y;
    private int z;

    public void setSign(String text, int x, int y, int z) {
        this.text = text;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public String getText() {
        return text;
    }

    public boolean sameSign(int x, int y, int z) {
        return this.x == x && this.y == y && this.z == z;
    }
}
