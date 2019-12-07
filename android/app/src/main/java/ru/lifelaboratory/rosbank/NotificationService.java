package ru.lifelaboratory.rosbank;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.List;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.lifelaboratory.rosbank.entity.Notification;

public class NotificationService extends Service {

    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(new Runnable() {
            public void run() {
                while(true) {
                    if (MainActivity.server != null) {
                        ServerAPI notification = MainActivity.server.create(ServerAPI.class);
                        notification.getNotification(MainActivity.user.getIdUser())
                                .enqueue(new Callback<List<Notification>>() {
                                    @Override
                                    public void onResponse(Call<List<Notification>> call, Response<List<Notification>> response) {
                                        Log.e("ROSBANK2019", String.valueOf(response.body().size()));
                                        if (response.body() != null) {
                                            for (int i = 0; i < response.body().size(); i++) {
                                                Log.e("ROSBANK2019", response.body().get(i).getName());
                                                Intent toMainActivity = new Intent(getApplicationContext(), MainActivity.class)
                                                        .setAction("lifelaboratory.from_notification_service");
                                                if (response.body().get(i).getType() != null && response.body().get(i).getType().equals(2)) {
                                                    toMainActivity.putExtra("TYPE", 2);
                                                    StringBuilder image = new StringBuilder();
                                                    StringBuilder description = new StringBuilder();
                                                    for (int j = 0; j < response.body().get(i).getImage().size(); j++){
                                                        image.append(response.body().get(i).getImage().get(j)).append(";");
                                                        description.append(response.body().get(i).getDescription().get(j)).append(";");
                                                    }
                                                    toMainActivity.putExtra("image", image.toString());
                                                    toMainActivity.putExtra("description", description.toString());
                                                }
                                                PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0,
                                                        toMainActivity, PendingIntent.FLAG_UPDATE_CURRENT);
                                                NotificationCompat.Builder builder =
                                                        new NotificationCompat.Builder(getApplicationContext())
                                                                .setSmallIcon(R.mipmap.ic_launcher)
                                                                .setContentTitle(response.body().get(i).getName())
                                                                .setContentText(response.body().get(i).getUrl())
                                                                .setContentIntent(contentIntent);
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
                    } else {
                        break;
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
