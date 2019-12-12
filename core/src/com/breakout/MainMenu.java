package com.breakout;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class MainMenu implements Screen {

    BitmapFont font;
    SpriteBatch spriteBatch;
    Breakout game;

    MainMenu(Breakout game) {
        this.game = game;
        font = new BitmapFont();
        spriteBatch = new SpriteBatch();
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        game.batch.begin();
        game.font.getData().markupEnabled = true;
        game.font.draw(game.batch, "Welcome to [#FFAA00]Breakout", 175, 300);
        game.font.draw(game.batch, "A small game by [#FF0000]Min Ho Gang [#FFFFFF]and [#0000FF]Karl Adriano", 100, 250);
        game.font.draw(game.batch, "Press any key to begin", 175, 200);
        game.batch.end();

        if (Gdx.input.justTouched() || Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            game.setScreen(new GameScreen(game));
            dispose();
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit();
        }

    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {

    }

}
