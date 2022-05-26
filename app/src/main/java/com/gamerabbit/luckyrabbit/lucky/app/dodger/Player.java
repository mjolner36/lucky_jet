package com.gamerabbit.luckyrabbit.lucky.app.dodger;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class Player
{
    private Bitmap bitmap;
    private int x;
    private int y;
    private int xVelocity;

    public Player(Bitmap bitmap, int x, int y)
    {
        this.bitmap = bitmap;
        this.x = x;
        this.y = y;
    }

    public Bitmap getBitmap()
    {
        return bitmap;
    }

    public int getX()
    {
        return x;
    }

    public int getY()
    {
        return y;
    }

    public int getXVelocity()
    {
        return xVelocity;
    }

    public void setBitmap(Bitmap bitmap)
    {
        this.bitmap = bitmap;
    }

    public void setXVelocity(int newXVelocity)
    {
        xVelocity = newXVelocity;
    }

    public void draw(Canvas canvas)
    {
        canvas.drawBitmap(bitmap, x, y, null);
        x += xVelocity;
    }
}
