package com.example.metroapp_v1;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import static android.content.ContentValues.TAG;

public class MyReceiver extends BroadcastReceiver {

    private Context context;


    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;


        Log.i("qqqq","done");

        Intent in=new Intent(context,MainActivity.class);
        PendingIntent pe=PendingIntent.getActivity(context,0,in,0);


        createNotificationChannel();

        //show notification
        NotificationCompat.Builder builder=new NotificationCompat.Builder(context,"apppp");
        builder.setTicker("new message")
                .setContentTitle("new message")
                .setContentText("wordssdsdadasdasdsadsadsadsadasdsadas")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true)
                .setContentIntent(pe);

        Notification notification = builder.build();

        NotificationManager manager= (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(1,notification);

//        Intent in=new Intent();

//       MyService.enqueueWork(context,in);
    }
    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "apppp";
            String description = "apppp";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("apppp", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}