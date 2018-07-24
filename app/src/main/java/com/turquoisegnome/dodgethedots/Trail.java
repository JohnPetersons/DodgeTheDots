package com.turquoisegnome.dodgethedots;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

/**
 * Created by TurquoiseGnome on 6/26/2018.
 */

public class Trail {

    private Path path;
    private Paint paint;
    public Trail(Path path, Paint paint){
        this.path = path;
        this.paint = paint;
    }

    public void draw(Canvas canvas){
        canvas.drawPath(this.path, this.paint);
    }

    public void move(float  x, float y){
        this.path.offset((float)(x * GameView.getDeltaTime()), (float)(y * GameView.getDeltaTime()));
    }
}
