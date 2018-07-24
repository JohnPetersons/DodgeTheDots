package com.turquoisegnome.dodgethedots;

import android.graphics.Bitmap;

/**
 * Created by TurquoiseGnome on 6/23/2018.
 */

public class Enemy extends GameObject {

    private float xTarget, yTarget, xOrigin, yOrigin, xSpeed, ySpeed;
    private static final float MAX_SPEED = 800;
    public static final float MAX_ACCEL = 25;

    public Enemy(Bitmap image, float x, float y, float xTarget, float yTarget, int scale){
        super(image, x, y, scale);
        this.xTarget = xTarget;
        this.yTarget = yTarget;
        this.xOrigin = xTarget;
        this.yOrigin = yTarget;
        this.xSpeed = 0;
        this.ySpeed = 0;
    }

    @Override
    public void update(){
        super.update();
        this.move(this.xSpeed, this.ySpeed);
    }

    public void playerMove(float x, float y){
        this.move(x, y);
        this.path.offset((float)(x * GameView.getDeltaTime()), (float)(y * GameView.getDeltaTime()));
        this.xTarget = this.xOrigin - x * (float)GameView.getDeltaTime();
        this.yTarget = this.yOrigin - y * (float)GameView.getDeltaTime();
        float tempX = this.xTarget - this.x;
        float tempY = this.yTarget - this.y;
        float c = (float)Math.sqrt(tempX * tempX + tempY * tempY);
        if (c > Enemy.MAX_ACCEL){
            tempX *= Enemy.MAX_ACCEL / c;
            tempY *= Enemy.MAX_ACCEL / c;
        }
        this.xSpeed += tempX;
        this.ySpeed += tempY;
        c = (float)Math.sqrt(this.xSpeed * this.xSpeed + this.ySpeed * this.ySpeed);
        if (c > Enemy.MAX_SPEED){
            this.xSpeed *= Enemy.MAX_SPEED / c;
            this.ySpeed *= Enemy.MAX_SPEED / c;
        }
    }
}
