package com.general.dailyplanning.components;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.general.dailyplanning.R;
import com.general.dailyplanning.activities.MainActivity;
import com.general.dailyplanning.activities.UsingActivity;
import com.general.dailyplanning.data.DataManipulator;
import com.general.dailyplanning.data.Task;
import com.general.dailyplanning.data.Vault;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TaskService extends Service {
    final String TAG = "testing";
    // Variables
    private int id = 1;
    private Context mContext;
    private Vault mVault;
    private Handler mHandler;

    // Notification's vars
    private NotificationCompat.Builder builder;
    private Notification notification;
    private NotificationManager notificationManager;

    @Override
    public void onCreate() {
        super.onCreate();
        notificationManager = (NotificationManager) getSystemService(Service.NOTIFICATION_SERVICE);
        mHandler = new Handler();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: ");
        mContext = this;
        mVault = DataManipulator.loading(this, "data");
        int seconds = Integer.parseInt(new SimpleDateFormat("ss", Locale.US).format(new Date()));

        if (!MainActivity.isIsRunning()) {
            mHandler.postDelayed(taskRunnable, (60 - seconds) * 1000);
        } else {
            mHandler.removeCallbacksAndMessages(null);
        }

        return START_STICKY;
    }


    final Runnable taskRunnable = new Runnable(){
        public void run(){
            Log.d(TAG, "run: ");
            // Get current time
            String time = new SimpleDateFormat("HH:mm", Locale.US).format(new Date());
            Task task;

            while ((task = mVault.pushTimeLine(time)) != null) {
                builder = new NotificationCompat.Builder(mContext)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(task.toString().substring(0, 5))
                        .setContentText(task.getTask())
                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
                notification = builder.build();

                notificationManager.notify(id++, notification);
                // Save data
                DataManipulator.saving(mContext,"data", mVault);
            }

            // Repeat every 1 minute
            mHandler.postDelayed(taskRunnable, 60000);
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
