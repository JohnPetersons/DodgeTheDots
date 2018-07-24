package com.turquoisegnome.dodgethedots;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.SurfaceHolder;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;

/**
 * Created by TurquoiseGnome on 6/22/2018.
 */

public class GameView extends SurfaceView implements SurfaceHolder.Callback {

    private Context context;
    public MainThread thread;
    public static GameView main;
    private ArrayList<GameObject> gameObjects;
    private ArrayList<GameObject> gameObjectsToRemove;
    private Player player;
    private float x, y;
    public float width, height;
    private long lastTime;
    private double deltaTime, timer, enemyTimer;
    private boolean playing, endScreen;
    private FrameLayout parentLayout;
    private TextView timerView, highscoreView;
    private ArrayList<Trail> trails;
    private MenuButton start, yellow, blue, red;
    private Bitmap playerBitmap, titleBitmap;
    private int playerColor;

    public static final float SENSITIVITY = 3;

    public GameView(FrameLayout parent, Context context){
        super(context);

        getHolder().addCallback(this);
        this.context = context;
        this.parentLayout = parent;
        this.playing = false;
        this.endScreen = false;
        setFocusable(true);
        GameView.main = this;
        this.gameObjects = new ArrayList<>();
        this.gameObjectsToRemove = new ArrayList<>();
        this.width = Resources.getSystem().getDisplayMetrics().widthPixels;
        this.height = Resources.getSystem().getDisplayMetrics().heightPixels;
        this.playerBitmap = BitmapFactory.decodeResource(main.getResources(),R.drawable.playerblue);
        this.titleBitmap = BitmapFactory.decodeResource(main.getResources(),R.drawable.dodgethedotstitle);
        int temp = (int)((this.height - this.width) / 4.0f);
        int tempX = (int)(this.titleBitmap.getWidth() * (((float)temp) / this.titleBitmap.getHeight()));
        this.titleBitmap =  Bitmap.createScaledBitmap(this.titleBitmap, tempX, temp, false);
        this.playerColor = Color.BLUE;
        this.start = new MenuButton(BitmapFactory.decodeResource(main.getResources(),R.drawable.playblue), 0, (int)(this.height/2 - this.width * 0.1), (int)this.width, (int)(this.height/2 + this.width * 0.1));
        this.blue = new MenuButton(BitmapFactory.decodeResource(main.getResources(),R.drawable.playerblue), (int)(this.width * 0.02), (int)(this.height/2 + this.width * 0.2), (int)(this.width / 3 - this.width * 0.02), (int)(this.height/2 + this.width * 0.3));
        this.red = new MenuButton(BitmapFactory.decodeResource(main.getResources(),R.drawable.playerred), (int)(this.width / 3 + this.width * 0.02), (int)(this.height/2 + this.width * 0.2), (int)( 2 * this.width / 3 - this.width * 0.02), (int)(this.height/2 + this.width * 0.3));
        this.yellow = new MenuButton(BitmapFactory.decodeResource(main.getResources(),R.drawable.playeryellow), (int)(2 * this.width / 3 + this.width * 0.02), (int)(this.height/2 + this.width * 0.2), (int)(this.width) - (int)(this.width * 0.02), (int)(this.height/2 + this.width * 0.3));
        this.lastTime = System.nanoTime();
        this.timer = 0.0;
        this.enemyTimer = 0.0;
        this.trails = new ArrayList<>();
        this.timerView = new TextView(this.context);
        this.timerView.setTextColor(Color.WHITE);
        this.timerView.setTypeface(Typeface.MONOSPACE, Typeface.BOLD);
        this.timerView.setTextSize(this.width * 0.012f);
        this.timerView.setX(25);
        this.timerView.setY((this.height - this.width) / 2  - 4 * (this.width * 0.012f));
        this.parentLayout.addView(this);
        this.parentLayout.addView(this.timerView);
        this.highscoreView = new TextView(this.context);
        this.highscoreView.setTextColor(Color.WHITE);
        this.highscoreView.setTypeface(Typeface.MONOSPACE, Typeface.BOLD);
        this.highscoreView.setTextSize(this.width * 0.012f);
        this.parentLayout.addView(this.highscoreView);

        try {
            File file = new File(main.context.getFilesDir().getAbsolutePath() + "highscore.txt");
            if (!file.exists()) {
                file.createNewFile();
                this.highscoreView.setText("0 seconds");
            } else {
                int length = (int) file.length();
                byte[] bytes = new byte[length];
                FileInputStream input = new FileInputStream(file);
                input.read(bytes);
                input.close();
                String highscore = new String(bytes);
                highscore = "High Score: " + highscore + " seconds";
                this.highscoreView.setText(highscore);
            }
            this.highscoreView.measure(0,0);
            this.highscoreView.setX((int)(this.width - this.highscoreView.getMeasuredWidth() - 25));
            this.highscoreView.setY((this.height - this.width) / 2 - 4 * (this.width * 0.012f));
        } catch (Exception e) {

        }
    }

    public static double getDeltaTime(){
        return main.deltaTime;
    }

    public static void addGameObject(GameObject gameObject){
        main.gameObjects.add(gameObject);
    }

    public static void removeGameObject(GameObject gameObject){
        main.gameObjectsToRemove.add(gameObject);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        thread = new MainThread(getHolder());
        thread.setRunning(true);
        thread.start();
        this.lastTime = System.nanoTime();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        boolean retry = true;
        while (retry) {
            try {
                thread.setRunning(false);
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            retry = false;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        if (this.playing) {
            switch (e.getAction()) {
                case MotionEvent.ACTION_DOWN: {
                    this.x = e.getX();
                    this.y = e.getY();
                    return true;
                } case MotionEvent.ACTION_MOVE: {
                    float x = (e.getX() - this.x) * GameView.SENSITIVITY;
                    float y = (e.getY() - this.y) * GameView.SENSITIVITY;
                    this.player.accel(x, y);
                    this.x = e.getX();
                    this.y = e.getY();
                }
            }
        } else if (this.endScreen){
            switch  (e.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    GameView.leaveEndScreen();
            }
        } else {
            switch (e.getAction()){
                case MotionEvent.ACTION_DOWN: {
                    if (this.start.collide(e.getX(), e.getY())) {
                        this.startGame();
                        this.x = e.getX();
                        this.y = e.getY();
                    } else if (this.blue.collide(e.getX(), e.getY())) {
                        this.blue.press();
                        this.start = new MenuButton(BitmapFactory.decodeResource(main.getResources(), R.drawable.playblue), 0, (int) (this.height / 2 - this.width * 0.1), (int) this.width, (int) (this.height / 2 + this.width * 0.1));
                        this.playerBitmap = BitmapFactory.decodeResource(main.getResources(), R.drawable.playerblue);
                        this.playerColor = Color.BLUE;
                    } else if (this.red.collide(e.getX(), e.getY())) {
                        this.red.press();
                        this.start = new MenuButton(BitmapFactory.decodeResource(main.getResources(), R.drawable.playred), 0, (int) (this.height / 2 - this.width * 0.1), (int) this.width, (int) (this.height / 2 + this.width * 0.1));
                        this.playerBitmap = BitmapFactory.decodeResource(main.getResources(), R.drawable.playerred);
                        this.playerColor = Color.RED;
                    } else if (this.yellow.collide(e.getX(), e.getY())) {
                        this.yellow.press();
                        this.start = new MenuButton(BitmapFactory.decodeResource(main.getResources(), R.drawable.playgreen), 0, (int) (this.height / 2 - this.width * 0.1), (int) this.width, (int) (this.height / 2 + this.width * 0.1));
                        this.playerBitmap = BitmapFactory.decodeResource(main.getResources(), R.drawable.playeryellow);
                        this.playerColor = Color.GREEN;
                    }
                    return true;
                } case MotionEvent.ACTION_UP: {
                    this.blue.unpress();
                    this.red.unpress();
                    this.yellow.unpress();
                }
            }
        }
        return true;
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (canvas != null) {
            canvas.drawColor(Color.WHITE);
            for (int i = 0; i < this.trails.size(); i++){
                this.trails.get(i).draw(canvas);
            }
            for (int i = 0; i < this.gameObjects.size(); i++){
                this.gameObjects.get(i).draw(canvas);
            }
            if (!this.playing && !this.endScreen){
                this.start.draw(canvas);
                this.blue.draw(canvas);
                this.red.draw(canvas);
                this.yellow.draw(canvas);
            }
            if (this.player != null)
                this.player.draw(canvas);
            Paint paint = new Paint();
            paint.setColor(Color.BLACK);
            float temp = (this.height - this.width) / 2;
            canvas.drawRect(0, 0, this.width, temp, paint);
            canvas.drawRect(0, this.height - temp, this.width, this.height * 2, paint);
            canvas.drawBitmap(this.titleBitmap, this.width / 4, 0, null);
        }
    }

    public void update(){
        long tempTime = System.nanoTime();
        this.deltaTime = (double)((tempTime - this.lastTime)/1000000000.0f);
        this.lastTime = tempTime;
        if (this.playing) {
            this.timer += this.deltaTime;
            this.enemyTimer += this.deltaTime;
            int seconds = (int)this.timer;
            int milliseconds1 = (int)(this.timer * 10 - seconds * 10);
            int milliseconds2 = (int)(this.timer * 100 - seconds * 100 - milliseconds1 * 10);
            String message = seconds + "." + milliseconds1 + "" + milliseconds2 + " seconds";
            this.timerView.setText(message);
            if (this.enemyTimer > 1.5f){
                this.spawnEnemy();
                this.enemyTimer = 0.0;
            }
            this.player.update();
            for (int i = 0; i < this.trails.size(); i++){
                this.trails.get(i).move(this.player.getXMove() * -1, this.player.getYMove() * -1);
            }
            for (int i = 0; i < this.gameObjects.size(); i++) {
                GameObject g1 = this.gameObjects.get(i);
                if (this.gameObjects.get(i) instanceof Enemy) {
                    ((Enemy) this.gameObjects.get(i)).playerMove(this.player.getXMove() * -1, this.player.getYMove() * -1);
                    float tempX = g1.x - this.player.x;
                    float tempY = g1.y - this.player.y;
                    if (Math.sqrt(tempX * tempX + tempY * tempY) < g1.radius + this.player.radius) {
                        this.endGame();
                        break;
                    }
                } else
                    this.gameObjects.get(i).move(this.player.getXMove() * -1, this.player.getYMove() * -1);
                this.gameObjects.get(i).update();
                for (int j = 0; j < this.gameObjects.size(); j++) {
                    GameObject g2 = this.gameObjects.get(j);
                    if (i != j){
                        float tempX = g1.x - g2.x;
                        float tempY = g1.y - g2.y;
                        if (Math.sqrt(tempX * tempX + tempY * tempY) < g1.radius + g2.radius){
                            if (!this.gameObjectsToRemove.contains(g1))
                                this.gameObjectsToRemove.add(g1);
                            if (!this.gameObjectsToRemove.contains(g2))
                                this.gameObjectsToRemove.add(g2);
                        }
                    }
                }
            }
            if (this.playing) {
                for (int i = 0; i < this.gameObjectsToRemove.size(); i++) {
                    Trail t = this.gameObjectsToRemove.get(i).getTrail();
                    if (t != null)
                        this.trails.add(t);
                    this.gameObjects.remove(this.gameObjectsToRemove.get(i));
                    if (i < this.gameObjectsToRemove.size() / 2)
                        this.spawnEnemy();
                }
                if (this.gameObjectsToRemove.size() > 0)
                    this.gameObjectsToRemove = new ArrayList<>();
            }
        }
    }

    public void spawnEnemy(){
        float temp = (this.height - this.width) / 2;
        float c = (float)Math.sqrt((this.width / 2) * (this.width / 2) + (this.width / 2) * (this.width / 2));
        float tempX = (float)Math.random() * this.width - this.width / 2;
        float tempY = (float)Math.random() * this.width - this.width / 2;
        float tempC = (float)Math.sqrt(tempX * tempX + tempY * tempY);
        tempX *= c / tempC;
        tempY *= c / tempC;
        tempX += this.width / 2;
        tempY += this.width / 2 + temp;
        new Enemy(BitmapFactory.decodeResource(main.getResources(),R.drawable.bubble), tempX,tempY,this.width / 2,this.width / 2 + temp, (int)(this.width * 0.03f));
    }

    public static void startGame(){
        main.timerView.setText("0 seconds");
        //main.timerView.setX(25);
        //main.timerView.setY((main.height - main.width) / 2 + 25);
        main.playing = true;
        main.timer = 0.0;
        main.enemyTimer = 0.0;
        main.gameObjects = new ArrayList<>();
        main.player = new Player(main.playerBitmap, (float)(main.width/2.0), (float)(main.height/2.0), (int)(main.width * 0.035f));
        main.player.paint.setColor(main.playerColor);
        main.gameObjects.remove(main.player);
    }

    public static void endGame(){
        for (int i = 0; i < main.gameObjects.size(); i++){
            main.trails.add(main.gameObjects.get(i).getTrail());
        }
        main.trails.add(main.player.getTrail());
        //main.timerView.measure(0, 0);
        //main.timerView.setX((int)(main.width / 2.0 - main.timerView.getMeasuredWidth() / 2));
        //main.timerView.setY(main.height / 2 - main.timerView.getMeasuredHeight() / 2);
        main.playing = false;
        main.endScreen = true;
        main.player = null;
        main.gameObjects = new ArrayList<>();
        main.gameObjectsToRemove = new ArrayList<>();
        try {
            File file = new File(main.context.getFilesDir().getAbsolutePath() + "highscore.txt");
            if (!file.exists())
                file.createNewFile();

            int length = (int) file.length();
            byte[] bytes = new byte[length];
            FileInputStream input = new FileInputStream(file);
            input.read(bytes);
            input.close();
            String contents = new String(bytes);
            if (contents.length() == 0)
                contents = "0.00";
            if (Float.parseFloat(contents) < main.timer){
                FileOutputStream output = new FileOutputStream(file);
                int seconds = (int)main.timer;
                int milliseconds1 = (int)(main.timer * 10 - seconds * 10);
                int milliseconds2 = (int)(main.timer * 100 - seconds * 100 - milliseconds1 * 10);
                String message = "High Score: " + seconds + "." + milliseconds1 + "" + milliseconds2;
                byte[] contentInBytes = message.getBytes();

                output.write(contentInBytes);
                output.flush();
                output.close();
                main.highscoreView.setText(message + " seconds");
                main.highscoreView.measure(0,0);
                main.highscoreView.setX((int)(main.width - main.highscoreView.getMeasuredWidth() - 25));
                main.highscoreView.setY((main.height - main.width) / 2 + 25);
            }
        } catch (Exception e) {

        }
    }

    public static void leaveEndScreen(){
        main.trails = new ArrayList<>();
        main.timerView.setText("");
        main.endScreen = false;
    }
}
