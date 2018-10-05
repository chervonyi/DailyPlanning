package com.general.dailyplanning.components;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Handler;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.general.dailyplanning.R;
import com.general.dailyplanning.data.DataManipulator;
import com.general.dailyplanning.data.Task;
import com.general.dailyplanning.data.Vault;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TaskService extends Service {
    private int id = 1;
    private Context mContext;
    private Vault mVault;
    private Handler mHandler;
    private NotificationCompat.Builder builder;
    private Notification notification;
    private NotificationManager notificationManager;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mHandler = new Handler();
        mContext = this;
        mVault = DataManipulator.loading(this, "data");
        mHandler.postDelayed( ToastRunnable, 0);

        return START_STICKY_COMPATIBILITY;
    }


    final Runnable ToastRunnable = new Runnable(){
        public void run(){
            
            // Get current time
            String time = new SimpleDateFormat("HH:mm", Locale.US).format(new Date());
            Task task;


            while ((task = mVault.pushTimeLine(time)) != null) {
                // TODO: Fix problem with notificationManager declaration

                builder = new NotificationCompat.Builder(mContext)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(task.getHours() + ":" + task.getMinutes())
                        .setContentText(task.getTask())
                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

                notification = builder.build();

                notificationManager.notify(id++, notification);

                Log.d("testing", "removed " + task.getTask());
                // Save data
                DataManipulator.saving(mContext,"data", mVault);
            }
            Log.d("testing", "count of tasks: " + mVault.getArray().size());

            // Repeat every 1 minute
            mHandler.postDelayed( ToastRunnable, 60000);
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
