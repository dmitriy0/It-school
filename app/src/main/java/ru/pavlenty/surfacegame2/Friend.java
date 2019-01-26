package ru.pavlenty.surfacegame2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

import java.util.Random;

public class Friend {
    private int x;
    private int y;
    private Bitmap bitmap;
    private int maxY;
    private int maxX;
    private int health;
    private Rect detectCollision;
    Friend(Context context, int screenX, int screenY) {
        maxX = screenX;
        maxY = screenY;
        Random generator = new Random();
        x = maxX;
        health = 3;
        y = generator.nextInt(maxY);
        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.enemy);
        detectCollision =  new Rect(x, y, bitmap.getWidth(), bitmap.getHeight());
    }
    public Bitmap getBitmap() {
        return bitmap;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getHealth(){return health;}
    public void setHealth(int hes){this.health += hes;}
    public int getY() {
        return y;
    }
    public void update(int playerSpeed) {
        x -= (playerSpeed+20);
        if (x < 0) {
            health -= 1;
            x = maxX;
            Random generator = new Random();
            y = generator.nextInt(maxY);
        }

            detectCollision.left = x;
            detectCollision.top = y;
            detectCollision.right = x + bitmap.getWidth();
            detectCollision.bottom = y + bitmap.getHeight();

    }
    public Rect getDetectCollision() {
        return detectCollision;
    }
}
