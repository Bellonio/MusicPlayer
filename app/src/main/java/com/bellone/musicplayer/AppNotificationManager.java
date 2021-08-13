package com.bellone.musicplayer;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;

public class AppNotificationManager {

    public static final String INTENT_FLAG_PREV = "1";
    public static final String INTENT_FLAG_PLAYPAUSE = "2";
    public static final String INTENT_FLAG_NEXT = "3";

    //È tutto static xke mi serve che sia accessibile dalla classe
    // innestata NotifReceiver che è static
    @SuppressLint("StaticFieldLeak")
    private static MainActivity padre_mainActivity;
    private static NotificationManager manager;
    @SuppressLint("StaticFieldLeak")
    private static NotificationCompat.Builder builder;

    private static RemoteViews views;

    public AppNotificationManager(Context context, MainActivity padre_mainActivity) {
        AppNotificationManager.padre_mainActivity = padre_mainActivity;

        //(in realtà qui, nel costruttore, non entrerà mai se la versione di android è troppo
        // bassa xke ho già fatto il controllo in MainActivity, però devo cmq fare questo
        // controllo per rimuovere gli errori)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            manager = context.getSystemService(NotificationManager.class);
            views = new RemoteViews(context.getPackageName(), R.layout.notification_layout);

            views.setImageViewResource(R.id.imgPrev_notif, R.drawable.previous);
            views.setImageViewResource(R.id.imgPlayPause_notif, R.drawable.not_pause);
            views.setImageViewResource(R.id.imgNext_notif, R.drawable.next);

            NotificationChannel channel = new NotificationChannel("musicController", "musicController",
                    NotificationManager.IMPORTANCE_DEFAULT);
            manager.createNotificationChannel(channel);

            builder = new NotificationCompat.Builder(context, "musicController");
            builder.setSilent(true);
            builder.setSmallIcon(R.drawable.ic_music_notif);
            builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);

            Intent intent = new Intent(context, NotifReceiver.class);

            intent.setAction(INTENT_FLAG_PREV);
            views.setOnClickPendingIntent(R.id.imgPrev_notif,
                    PendingIntent.getBroadcast(context, 0,
                            intent, PendingIntent.FLAG_UPDATE_CURRENT));

            intent.setAction(INTENT_FLAG_PLAYPAUSE);
            views.setOnClickPendingIntent(R.id.imgPlayPause_notif,
                    PendingIntent.getBroadcast(context, 0,
                            intent, PendingIntent.FLAG_UPDATE_CURRENT));

            intent.setAction(INTENT_FLAG_NEXT);
            views.setOnClickPendingIntent(R.id.imgNext_notif,
                    PendingIntent.getBroadcast(context, 0,
                            intent, PendingIntent.FLAG_UPDATE_CURRENT));
        }
    }

    public static void show_notif(){
        builder.setCustomContentView(views);
        manager.notify(1325, builder.build());
    }

    public static void change_image_to_pause(){
        views.setImageViewResource(R.id.imgPlayPause_notif, R.drawable.not_pause);
        show_notif();
    }
    public static void change_image_to_play(){
        views.setImageViewResource(R.id.imgPlayPause_notif, R.drawable.not_play);
        show_notif();
    }
    public void update_music_name(String txt){
        views.setTextViewText(R.id.lblMusicName_notif, txt);
        show_notif();
    }


    public void cancelNotification() { manager.cancel(1325); }


    public static class NotifReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            try{
                if(intent.getAction().equals(AppNotificationManager.INTENT_FLAG_PREV)){
                    padre_mainActivity.previous();
                }else if(intent.getAction().equals(AppNotificationManager.INTENT_FLAG_PLAYPAUSE)){
                    if(padre_mainActivity.musicIsPlaying()){
                        padre_mainActivity.pause();
                        change_image_to_play();
                    }else{
                        padre_mainActivity.play();
                        change_image_to_pause();
                    }
                }else if(intent.getAction().equals(AppNotificationManager.INTENT_FLAG_NEXT)){
                    padre_mainActivity.next();
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }
}
