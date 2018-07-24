package com.turquoisegnome.dodgethedots;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;

/**
 * Created by TurquoiseGnome on 6/22/2018.
 */

public abstract class GameObject {

    protected Bitmap image, scaledImage;
    public float x, y;
    public float radius;
    public Paint paint;
    public Path path;

    public GameObject(Bitmap image, float x, float y, int scale){
        GameView.addGameObject(this);
        this.image = image;
        this.scaledImage = Bitmap.createScaledBitmap(image, scale, scale, false);
        this.x = x;
        this.y = y;
        this.radius = this.scaledImage.getWidth() / 2.0f;
        this.paint = new Paint();
        this.paint.setColor(Color.BLACK);
        this.paint.setStrokeWidth(5);
        this.paint.setStyle(Paint.Style.STROKE);
        this.path = new Path();
        this.path.moveTo(x, y);
    }

    public void draw(Canvas canvas){
        if (this.path != null)
            canvas.drawPath(this.path, this.paint);
        canvas.drawBitmap(this.scaledImage, (int)(this.x - this.scaledImage.getWidth() / 2.0), (int)(this.y - this.scaledImage.getHeight() / 2.0), null);
    }

    public Trail getTrail(){
        if (this.path == null)
            return null;
        return new Trail(this.path, this.paint);
    }

    public void update(){
        this.path.lineTo(this.x, this.y);
    }

    public void move(float x, float y){
        this.x += x * GameView.getDeltaTime();
        this.y += y * GameView.getDeltaTime();
    }


}
