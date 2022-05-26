package com.gamerabbit.luckyrabbit.lucky.app.dodger;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.gamerabbit.luckyrabbit.lucky.app.R;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {
    private GameThread thread;
    private Player player;
    private ArrayList<Projectile> projectiles;
    private ArrayList<Integer> projectilesToRemove;
    private int score;
    private int highScore;
    private Paint textColor;
    private Bitmap scaled;
    private boolean isGameOver;
    private Timer t;
    private int speed;
//    private MediaPlayer deathSound;

    public GameView(Context context) {
        super(context);
        getHolder().addCallback(this);
        thread = new GameThread(getHolder(), this);
        setFocusable(true);

        player = new Player(BitmapFactory.decodeResource(getResources(), R.drawable.rabbit), 50, 50);

        textColor = new Paint();
        textColor.setTextSize(20f);
        textColor.setColor(Color.WHITE);

        score = 0;
        highScore = 0;
        isGameOver = false;
        speed = 1000;

//        deathSound = MediaPlayer.create(this.getContext(), R.raw.death);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Bitmap background = BitmapFactory.decodeResource(getResources(), R.drawable.background_image);
        scaled = Bitmap.createScaledBitmap(background, getWidth(), getHeight(), true);
        player = new Player(BitmapFactory.decodeResource(getResources(), R.drawable.rabbit), getWidth() / 2, (2 * (getHeight() / 3)));

        thread.setRunning(true);
        if (thread.getState() == Thread.State.NEW) {
            thread.start();
        }

        projectiles = new ArrayList<Projectile>();
        projectilesToRemove = new ArrayList<Integer>();

        t = new Timer();
        t.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                generateProjectile();
            }
        }, 0, speed);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        while (retry) {
            try {
                thread.join();
                retry = false;
            } catch (InterruptedException e) {

            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            // move player
            if (event.getX() > getWidth() / 2) {
                player.setXVelocity(15);
            } else {
                player.setXVelocity(-15);
            }

            // restart game
            if (event.getX() >= 0 && event.getX() <= getWidth() && event.getY() >= 3 * (getHeight() / 8) && event.getY() <= 4 * (getHeight() / 8) && isGameOver) {
                resetGame();
            }

            // go back to main menu
            if (event.getX() >= 0 && event.getX() <= getWidth() && event.getY() >= 5 * (getHeight() / 8) && event.getY() <= 6 * (getHeight() / 8) && isGameOver) {
                thread.setRunning(false);
//                Intent intent = new Intent(getContext(), MenuActivity.class);
//                getContext().startActivity(intent);
            }
        }

        // stop the player from moving
        if (event.getAction() == MotionEvent.ACTION_UP) {
            player.setXVelocity(0);
        }

        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
//        System.out.println("Speed: " + speed);
        // prevent the player from going out of bounds
        if (player.getXVelocity() <= 0 && player.getX() <= 0) {
            player.setXVelocity(0);
        } else if (player.getXVelocity() >= 0 && player.getX() >= getWidth() - 100) {
            player.setXVelocity(0);
        }

        if (canvas != null) {
            canvas.drawBitmap(scaled, 0, 0, null);
            // canvas.drawColor(Color.BLUE);

            textColor.setTextSize(50f);
            canvas.drawText("Score: " + score, 5, 50, textColor);
            canvas.drawText("High Score: " + highScore, 5, 100, textColor);

            if (isGameOver) {
                textColor.setTextSize(50f);
//                canvas.drawText("GAME OVER", (getWidth() / 4), getHeight() / 4, textColor);

                Paint rect = new Paint();
                rect.setColor(Color.BLUE);

                canvas.drawRect(0, 3 * (getHeight() / 8), getWidth(), 4 * (getHeight() / 8), rect);
                canvas.drawText("Restart", getWidth() / 3, 7 * (getHeight() / 16) + 20, textColor);
            } else {
                player.draw(canvas);
                synchronized (projectiles) {
                    try {
                        for (Projectile projectile : projectiles) {
                            projectile.draw(canvas);
                            if (projectile.getY() > getHeight()) {
                                Integer integer = Integer.valueOf(projectiles.indexOf(projectile));
                                projectilesToRemove.add(integer);
                            }
                        }
                    } catch (ConcurrentModificationException e) {

                    }
                }
                for (Integer integer : projectilesToRemove) {
                    score++;
                    if (score % 10 == 0) {
                        if (speed >= 200) {
                            speed -= 100;
                        }
                        t.cancel();
                        t = new Timer();
                        t.scheduleAtFixedRate(new TimerTask() {
                            public void run() {
                                generateProjectile();
                            }
                        }, 0, speed);
                    }
                    projectiles.remove(integer.intValue());
                }
                projectilesToRemove.clear();
            }
            setHighScore(score);
        }
    }

    protected void collisionsCheck(Canvas canvas) {
        Bitmap playerBitmap = player.getBitmap();
        try {
            for (Projectile projectile : projectiles) {
                Bitmap projectileBitmap = projectile.getBitmap();
                if (projectile.getX() < player.getX() + playerBitmap.getWidth() && projectile.getX() + projectileBitmap.getWidth() > player.getX()) {
                    if (projectile.getY() < player.getY() + playerBitmap.getHeight() && projectile.getY() + projectileBitmap.getHeight() > player.getY()) {
                        isGameOver = true;
                        saveHighScore();
//                        deathSound.start();
                    }
                }
            }
        } catch (ConcurrentModificationException e) {

        }
    }

    public int getScore() {
        return score;
    }

    public int getHighScore() {
        return highScore;
    }

    public void setHighScore(int newHighScore) {
        if (newHighScore > highScore) {
            highScore = newHighScore;
        }
    }

    public void saveHighScore() {
        setHighScore(getScore());
        SharedPreferences sharedPref = ((Activity) getContext()).getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("Saved HighScore", getHighScore());
        editor.apply();
    }

    public void resetGame() {
        projectiles.clear();
        projectilesToRemove.clear();
        isGameOver = false;
        score = 0;
        speed = 1000;
        t.cancel();
        t = new Timer();
        t.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                generateProjectile();
            }
        }, 0, speed);
    }

    public void generateProjectile() {
        Random generator = new Random();
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.rabbit);
        int startingXPosition = generator.nextInt(getWidth() - bitmap.getWidth());
        Bitmap projectileBM = BitmapFactory.decodeResource(getResources(), R.drawable.hat);
        Projectile projectile = new Projectile(projectileBM, startingXPosition, 0);
        projectiles.add(projectile);
    }

}