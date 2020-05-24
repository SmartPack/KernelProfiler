package com.smartpack.kernelprofiler.utils;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.smartpack.kernelprofiler.R;
import com.smartpack.kernelprofiler.utils.root.RootUtils;

/**
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on May 22, 2020
 */

public class BootService extends Service {

    private static String onBoot = null;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (KP.supported()) {
            onBoot = KP.KPFile() + "/" + KP.getDefaultProfile();
            if (KP.isKPProfile(onBoot) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationManager mNotificationManager = (NotificationManager)
                        getSystemService(NOTIFICATION_SERVICE);
                NotificationChannel notificationChannel = new NotificationChannel("onboot",
                        getString(R.string.app_name), NotificationManager.IMPORTANCE_DEFAULT);
                notificationChannel.setSound(null, null);
                assert mNotificationManager != null;
                mNotificationManager.createNotificationChannel(notificationChannel);
                Notification.Builder builder = new Notification.Builder(
                        this, "onboot");
                builder.setContentTitle(getString(R.string.on_boot))
                        .setContentText(getString(R.string.on_boot_summary, KP.getDefaultProfile()))
                        .setSmallIcon(R.drawable.ic_launcher_preview);
                startForeground(1, builder.build());
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (KP.isKPProfile(onBoot)) {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    RootUtils.runCommand("sh " + onBoot);
                    return null;
                }
                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);
                    stopSelf();
                }
            }.execute();
        }
        return START_NOT_STICKY;
    }

}