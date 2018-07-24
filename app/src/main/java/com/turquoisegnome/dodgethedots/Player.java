package com.turquoisegnome.dodgethedots;


import android.graphics.Bitmap;
import android.graphics.Color;

/**
 * Created by TurquoiseGnome on 6/22/2018.
 */

public class Player extends GameObject {

    private float xMove, yMove;
    private static final float MAX_SPEED = 600;
    public static final float MAX_ACCEL = 200;

    public Player(Bitmap image, float x, float y, int scale){
        super(image, x, y, scale);
        this.xMove = 0;
        this.yMove = 0;
        this.paint.setColor(Color.BLUE);
    }

    @Override
    public void update(){
        super.update();
        this.path.offset((float)(this.xMove * -1 * GameView.getDeltaTime()), (float)(this.yMove * -1 * GameView.getDeltaTime()));
    }

    public void accel(float x, float y){
        float c = (float)Math.sqrt(x * x + y * y);
        if (c > Player.MAX_ACCEL){
            x *= Player.MAX_ACCEL/c;
            y *= Player.MAX_ACCEL/c;
        }
        this.xMove += x;
        this.yMove += y;
        c = (float)Math.sqrt(this.xMove * this.xMove + this.yMove * this.yMove);
        if (c > Player.MAX_SPEED){
            this.xMove *= Player.MAX_SPEED/c;
            this.yMove *= Player.MAX_SPEED/c;
        }
    }

    public float getXMove(){
        return this.xMove;
    }

    public float getYMove(){
        return this.yMove;
    }
}
