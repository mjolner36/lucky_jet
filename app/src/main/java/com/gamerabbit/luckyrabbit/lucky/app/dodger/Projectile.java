package com.gamerabbit.luckyrabbit.lucky.app.dodger;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class Projectile
{
    private Bitmap bitmap;
    private int x;
    private int y;

    public Projectile(Bitmap bitmap, int x, int y)
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

    public void setBitmap(Bitmap bitmap)
    {
        this.bitmap = bitmap;
    }

    public void setX(int newX)
    {
        this.x = newX;
    }

    public void setY(int newY)
    {
        this.y = newY;
    }

    public void draw(Canvas canvas)
    {
        canvas.drawBitmap(bitmap, x, y, null);
        y += 10;
    }
}
