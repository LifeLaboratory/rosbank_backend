package ru.lifelaboratory.rosbank;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.List;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationService extends Service {

    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(new Runnable() {
            public void run() {
                while(true) {
                    ServerAPI notification = MainActivity.server.create(ServerAPI.class);
                    notification.getNotification(MainActivity.user.getIdUser())
                            .enqueue(new Callback<List<Notification>>() {
                                @RequiresApi(api = Build.VERSION_CODES.O)
                                @Override
                                public void onResponse(Call<List<Notification>> call, Response<List<Notification>> response) {
                                    Log.e("ROSBANK2019", String.valueOf(response.body().size()));
                                    if (response.body() != null){
                                        for (int i = 0; i < response.body().size(); i++) {
                                            Log.e("ROSBANK2019", response.body().get(i).getName());
                                            NotificationCompat.Builder builder =
                                                    new NotificationCompat.Builder(getApplicationContext())
                                                            .setSmallIcon(R.mipmap.ic_launcher)
                                                            .setContentTitle(response.body().get(i).getName())
                                                            .setContentText(response.body().get(i).getUrl());
                                            android.app.Notification notification = builder.build();
                                            NotificationManager notificationManager =
                                                    (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                                            notificationManager.notify(1, notification);
                                        }
                                    }
                                }
                                @Override
                                public void onFailure(Call<List<Notification>> call, Throwable t) {
                                    Log.e("ROSBANK2019", t.getMessage());
                                }
                            });
                    try {
                        TimeUnit.SECONDS.sleep(10);
                    } catch (InterruptedException e) {
                        Log.e("ROSBANK2019", e.getMessage());
                    }
                }
            }
        }).start();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
