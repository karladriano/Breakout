package com.breakout;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class Block {
    public Image image;
    public int tier;
    float x, y;
    public Block(Image image, int tier, float x, float y) {
        this.image = image;
        this.tier = tier;
        this.x = x;
        this.y = y;
    }
}
