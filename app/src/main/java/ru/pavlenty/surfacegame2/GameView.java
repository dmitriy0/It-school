package ru.pavlenty.surfacegame2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;

public class GameView extends SurfaceView implements Runnable {

    volatile boolean playing;
    private Thread gameThread = null;
    private Player player;
    private Friend friend;
    private Enemy enemy;
    private boolean isCollision;


    private Paint paint;
    private Canvas canvas;
    private SurfaceHolder surfaceHolder;

    private ArrayList<Star> stars = new ArrayList<Star>();

    int screenX;
    int screenY;
    int countMisses;
    private int PlayerB;
    private int PlayerL;
    private int PlayerR;
    private int PlayerT;

    private int FriendB;
    private int FriendL;
    private int FriendR;
    private int FriendT;

    private int EnemyB;
    private int EnemyL;
    private int EnemyR;
    private int EnemyT;


    boolean flag ;


    private boolean isGameOver;

    int score;


    int highScore[] = new int[4];


    SharedPreferences sharedPreferences;
    private  Bitmap BoomBitmap;
    static MediaPlayer gameOnsound;
    final MediaPlayer killedEnemysound;
    final MediaPlayer gameOversound;

    Context context;

    public GameView(Context context, int screenX, int screenY) {
        super(context);
        player = new Player(context, screenX, screenY);
        friend = new Friend(context,screenX,screenY);
        enemy = new Enemy(context,screenX,screenY);
        surfaceHolder = getHolder();
        paint = new Paint();
        BoomBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.boom);

        int starNums = 100;
        for (int i = 0; i < starNums; i++) {
            Star s = new Star(screenX, screenY);
            stars.add(s);
        }

        this.screenX = screenX;
        this.screenY = screenY;
        countMisses = 0;
        isGameOver = false;


        score = 0;
        sharedPreferences = context.getSharedPreferences("SHAR_PREF_NAME", Context.MODE_PRIVATE);


        highScore[0] = sharedPreferences.getInt("score1", 0);
        highScore[1] = sharedPreferences.getInt("score2", 0);
        highScore[2] = sharedPreferences.getInt("score3", 0);
        highScore[3] = sharedPreferences.getInt("score4", 0);
        this.context = context;


        gameOnsound = MediaPlayer.create(context,R.raw.gameon);
        killedEnemysound = MediaPlayer.create(context,R.raw.killedenemy);
        gameOversound = MediaPlayer.create(context,R.raw.gameover);


        gameOnsound.start();
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_UP:
                player.stopBoosting();
                break;
            case MotionEvent.ACTION_DOWN:
                player.setBoosting();
                break;

        }

        if(isGameOver){
            if(motionEvent.getAction()==MotionEvent.ACTION_DOWN){
                context.startActivity(new Intent(context,MainActivity.class));
            }
        }
        return true;
    }

    @Override
    public void run() {
        while (playing) {
            update();
            draw(isCollision);
            control();
        }
    }

    public void draw(boolean de) {
        if (surfaceHolder.getSurface().isValid()) {
            canvas = surfaceHolder.lockCanvas();
            canvas.drawColor(Color.BLACK);


            paint.setColor(Color.WHITE);
            paint.setTextSize(20);

            for (Star s : stars) {
                paint.setStrokeWidth(s.getStarWidth());
                canvas.drawPoint(s.getX(), s.getY(), paint);
            }


            paint.setTextSize(30);
            canvas.drawText("Очки: "+score,100,50,paint);

            canvas.drawBitmap(
                    player.getBitmap(),
                    player.getX(),
                    player.getY(),
                    paint);
            canvas.drawBitmap(
                    friend.getBitmap(),
                    friend.getX(),
                    friend.getY(),
                    paint);
            canvas.drawBitmap(
                    enemy.getBitmap(),
                    enemy.getX(),
                    enemy.getY(),
                    paint);
            if (de){
                canvas.drawBitmap(
                        BoomBitmap,
                        enemy.getX(),
                        enemy.getY(),
                        paint);
            }
            if(isGameOver){
                paint.setTextSize(150);
                paint.setTextAlign(Paint.Align.CENTER);

                int yPos=(int) ((canvas.getHeight() / 2) - ((paint.descent() + paint.ascent()) / 2));
                canvas.drawText("Game Over",canvas.getWidth()/2,yPos,paint);
            }

            surfaceHolder.unlockCanvasAndPost(canvas);

        }
    }


    public static void stopMusic(){
        gameOnsound.stop();
    }

    private void update() {
        PlayerB = player.getDetectCollision().bottom;
        PlayerL = player.getDetectCollision().left;
        PlayerR = player.getDetectCollision().right;
        PlayerT = player.getDetectCollision().top;
        FriendB = friend.getDetectCollision().bottom;
        FriendL = friend.getDetectCollision().left;
        FriendR = friend.getDetectCollision().right;
        FriendT = friend.getDetectCollision().top;
        EnemyB = enemy.getDetectCollision().bottom;
        EnemyL = enemy.getDetectCollision().left;
        EnemyR = enemy.getDetectCollision().right;
        EnemyT = enemy.getDetectCollision().top;
        if ((PlayerB >= FriendT && PlayerT <= FriendB) && (PlayerL <= FriendR && PlayerR >= FriendL)){
            friend.setHealth(1);
            friend.setX(-1000);



        }
        if ((PlayerB >= EnemyT && PlayerT <= EnemyB) && (PlayerL <= EnemyR && PlayerR >= EnemyL)){

            stopMusic();
            isGameOver = true;
            isCollision = true;
            draw(true);
            pause();
        }

        score++;

        player.update();

        friend.update(player.getSpeed());
        if (friend.getHealth()<=0) {
            isGameOver = true;
            stopMusic();
            pause();

        }
        enemy.update(player.getSpeed());


        for (Star s : stars) {
            s.update(player.getSpeed());
        }
    }

    private void control() {
        try {
            gameThread.sleep(17);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void pause() {
        playing = false;
    }

    public void resume() {
        playing = true;
        gameThread = new Thread(this);
        gameThread.start();
    }


}