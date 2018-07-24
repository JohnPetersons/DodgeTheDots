package com.turquoisegnome.dodgethedots;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

/**
 * Created by TurquoiseGnome on 6/27/2018.
 */

public class MenuButton {

    private int left, top, right, bottom;
    private Bitmap image, scaledImage;
    private Paint paint;
    private boolean pressed;

    public MenuButton(Bitmap image, int left, int top, int right, int bottom){
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
        this.pressed = false;
        this.paint = new Paint();
        this.paint.setColor(Color.BLACK);
        int x = image.getWidth();
        int y = image.getHeight();
        if (x > right - left){
            y *= (float)((right - left) / (float)x);
            x = right - left;
        }
        if (y > bottom - top) {
            x *= (float)((bottom - top) / (float)y);
            y = bottom - top;
        }
        this.image = image;
        this.scaledImage = Bitmap.createScaledBitmap(image, x, y, false);
        this.left -= GameView.main.width * 0.01;
        this.right += GameView.main.width * 0.01;
        this.top -= GameView.main.width * 0.01;
        this.bottom += GameView.main.width * 0.01;
    }

    public void draw(Canvas canvas){
        canvas.drawRect(this.left, this.top, this.right, this.bottom, this.paint);
        canvas.drawBitmap(this.scaledImage, (this.right + this.left) / 2 - this.scaledImage.getWidth() / 2, (this.bottom + this.top) / 2 - this.scaledImage.getHeight() / 2, null);
    }

    public boolean collide(float x, float y){
        if (this.left < x && this.right > x && this.top < y && this.bottom > y)
            return true;
        return false;
    }

    public void press(){
        this.pressed = true;
        this.left += GameView.main.width * 0.02;
        this.right -= GameView.main.width * 0.02;
        this.top += GameView.main.width * 0.02;
        this.bottom -= GameView.main.width * 0.02;
        int x = image.getWidth();
        int y = image.getHeight();
        if (x > right - left){
            y *= (float)((right - left) / (float)x);
            x = right - left;
        }
        if (y > bottom - top) {
            x *= (float)((bottom - top) / (float)y);
            y = bottom - top;
        }
        this.scaledImage = Bitmap.createScaledBitmap(this.image, x, y, false);
        this.left -= GameView.main.width * 0.01;
        this.right += GameView.main.width * 0.01;
        this.top -= GameView.main.width * 0.01;
        this.bottom += GameView.main.width * 0.01;
    }

    public void unpress(){
        if (this.pressed){
            int x = image.getWidth();
            int y = image.getHeight();
            if (x > right - left){
                y *= (float)((right - left) / (float)x);
                x = right - left;
            }
            if (y > bottom - top) {
                x *= (float)((bottom - top) / (float)y);
                y = bottom - top;
            }
            this.scaledImage = Bitmap.createScaledBitmap(this.image, x, y, false);
            this.left -= GameView.main.width * 0.01;
            this.right += GameView.main.width * 0.01;
            this.top -= GameView.main.width * 0.01;
            this.bottom += GameView.main.width * 0.01;
            this.pressed = false;
        }
    }
}
