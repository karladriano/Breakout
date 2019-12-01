package com.breakout;

import com.badlogic.gdx.Game;
import com.breakout.Breakout;

public class Main extends Game {
    public void create() {
        this.setScreen(new Breakout());
    }
}
