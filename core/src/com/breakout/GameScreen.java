package com.breakout;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;

import java.util.HashMap;
import java.util.Iterator;

public class GameScreen implements Screen {
    Game parent;
    Stage stage;
    World world;
    Image ball, paddle;
    Body ballBody, wallBody, floorBody, paddleBody;
    HashMap<Body, Block> blocks;
    final int scale = 32;

    public GameScreen(Game parent) {
        this.parent = parent;
    }

    public void show() {
        stage = new Stage();
        blocks = new HashMap<>();

        Texture ballTexture = new Texture("ball.png");
        Texture tier1Block = new Texture("red_block.png");
        Texture tier2Block = new Texture("orange_block.png");
        Texture paddleTexture = new Texture("blue_pad.png");

        //An actor is a 2D graph node, which can have a position, rectangular size, origin, scale, etc.
        ball = new Image(ballTexture);
        stage.addActor(ball);
        paddle = new Image(paddleTexture);
        stage.addActor(paddle);

        final float screenWidth = Gdx.graphics.getWidth();
        final float screenHeight = Gdx.graphics.getHeight();
        final float blockWidth = tier1Block.getWidth();
        final float blockHeight = tier1Block.getHeight();

        world = new World(new Vector2(0, 0), true);

        for (float col = screenWidth / blockWidth; col >= 0; col--) {
            for (float row = screenHeight / blockHeight / 2 - 1; row > 0; row--) {
                Block block;
                Image image;
                Body blockBody;
                float x = col * blockWidth;
                float y = row * blockHeight + screenHeight / 2;
                if ((int)(row % 2) == 1) {
                    image = new Image(tier1Block);
                    block = new Block(image, 1, x, y);
                    blockBody = createRectBody(x, y, blockWidth, blockHeight);
                    blocks.put(blockBody, block);
                } else {
                    image = new Image(tier2Block);
                    block = new Block(image, 2, x, y);
                    blockBody = createRectBody(x, y, blockWidth, blockHeight);
                    blocks.put(blockBody, block);
                }
                image.setPosition(x, y);
                stage.addActor(image);
                blocks.put(blockBody, block);
            }
        }
        ballBody = createBallBody(100, 100, ballTexture.getWidth()/2);
        ballBody.setLinearVelocity(10, 10);

        wallBody = createRectBody(0, 0, screenWidth, screenHeight);
        floorBody = createRectBody(0, 0, screenWidth, 1);
        paddleBody = createRectBody(0, 0, blockWidth, blockHeight);

        Gdx.input.setInputProcessor(stage);
        //Move the paddle along with the mouse only in the x axis
        stage.addListener(new InputListener() {
            public boolean handle(Event e) {
                float x = Gdx.input.getX() - blockWidth / 2;
                moveBody(paddleBody, x, 0);
                return true;
            }
        });

        //This is fired when the ball collides with an object
        world.setContactListener(new ContactListener() {
            public void beginContact(Contact c) {
                Body b = c.getFixtureA().getBody();
                Block block = blocks.get(b);
                if (block != null) {
                    //If block is of a higher tier, bump it down and change the texture
                    if (block.tier >= 2) {
                        block.tier--;
                        block.image.setDrawable(new SpriteDrawable(new Sprite(new Texture("red_block.png"))));
                    } //If the block is of the lowest tier, this block is destroyed
                    else {
                        block.image.remove();
                    }
                    //Fired when the user fails to get the ball with the paddle
                } else if (b == floorBody) {
                    //Restart the game
                    show();
                }
            }
            public void endContact(Contact c) {}
            public void postSolve(Contact c, ContactImpulse ci) {}
            public void preSolve(Contact c, Manifold m) {}
        });
    }

    //Put the Objects on screen and destroy objects that do not have reference
    //e.g. no longer being used
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        world.step(delta, 10, 10);
        paddle.setPosition(paddleBody.getPosition().x*scale, paddleBody.getPosition().y*scale);
        ball.setPosition(ballBody.getPosition().x*scale, ballBody.getPosition().y*scale);

        Iterator iter = blocks.keySet().iterator();
        while (iter.hasNext()) {
            Body b = (Body) iter.next();
            Block block = blocks.get(b);
            if (!block.image.hasParent() && block.tier == 1) {
                world.destroyBody(b);
                iter.remove();
            }
        }
        stage.act(delta);
        stage.draw();
    }

    public void dispose() {
    }

    public void hide() {
    }

    public void pause() {
    }

    public void resize(int width, int height) {
    }

    public void resume() {
    }

    //Create the ball
    public Body createBallBody(float x, float y, float radius) {
        x = x/scale;
        y = y/scale;
        radius = radius/scale;

        BodyDef def = new BodyDef();
        def.type = BodyDef.BodyType.DynamicBody;
        def.position.set(x, y);
        Body body = world.createBody(def);

        CircleShape shape = new CircleShape();
        shape.setRadius(radius);
        shape.setPosition(new Vector2(radius, radius));
        Fixture fixture = body.createFixture(shape, 1);
        fixture.setFriction(0);
        fixture.setRestitution(1);
        shape.dispose();

        return body;
    }

    //Helper function to create rectangles in the screen. This is used to create bounded boxes
    //for the game screen, blocks and the paddle
    public Body createRectBody(float x, float y, float width, float height) {
        x = x/scale;
        y = y/scale;
        width = width/scale;
        height = height/scale;

        BodyDef def = new BodyDef();
        def.type = BodyDef.BodyType.StaticBody;
        def.position.set(x, y);
        Body body = world.createBody(def);

        ChainShape shape = new ChainShape();
        float[] vertices = {
                0, 0,
                0, height,
                width, height,
                width, 0,
                0, 0
        };
        shape.createChain(vertices);
        body.createFixture(shape, 1);
        shape.dispose();

        return body;
    }

    public void moveBody(Body body, float x, float y) {
        x = x/scale;
        y = y/scale;

        body.setTransform(x, y, 0);
    }
}
