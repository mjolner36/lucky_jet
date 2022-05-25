package com.gamelacky.luckyjet.lucky.app.dodger;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

import com.gamelacky.luckyjet.lucky.app.R;

public class BackgroundSoundService extends Service
{
    MediaPlayer player;

    public IBinder onBind(Intent arg0)
    {
        return null;
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        player = MediaPlayer.create(this, R.raw.sf);
        player.setLooping(true);
        player.setVolume(100,100);
    }

    public int onStartCommand(Intent intent, int flags, int startId)
    {
        player.start();
        return 1;
    }

    public IBinder onUnBind(Intent arg0)
    {
        return null;
    }

    public void onStop()
    {
        player.stop();
    }

    protected void onPause()
    {
        player.pause();
    }

    @Override
    public void onDestroy()
    {
        player.stop();
        player.release();
        player = null;
    }
}