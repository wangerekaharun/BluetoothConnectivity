package ke.co.appslab.bluetoothconnectivity.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import ke.co.appslab.bluetoothconnectivity.HomeActivity;
import ke.co.appslab.bluetoothconnectivity.R;
import ke.co.appslab.bluetoothconnectivity.utils.Constants;

public class BluetoothForegroundService extends Service{
    private static final String LOG_TAG = "BluetoothForeground";

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getAction().equals(Constants.START_FOREGROUND_ACTION)) {
            Log.i(LOG_TAG, "Received Start Foreground Intent ");
            //notification intent
            Intent notificationIntent = new Intent(this, HomeActivity.class);
            notificationIntent.setAction(Constants.MAIN_ACTION);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                    notificationIntent, 0);

            Intent startConnectionIntent = new Intent(this, BluetoothForegroundService.class);
            startConnectionIntent.setAction(Constants.START_CONNECTION_ACTION);
            PendingIntent previousPendingIntent = PendingIntent.getService(this, 0,
                    startConnectionIntent, 0);

            Intent stopConnectionIntent = new Intent(this, BluetoothForegroundService.class);
            stopConnectionIntent.setAction(Constants.STOP_CONNECTION_ACTION);

            PendingIntent pplayIntent = PendingIntent.getService(this, 0,
                    stopConnectionIntent, 0);


            Bitmap icon = BitmapFactory.decodeResource(getResources(),
                    R.drawable.ic_bluetooth_black_24dp);

            //show notification icon

            Notification notification = new NotificationCompat.Builder(this,"0")
                    .setContentTitle("Bluetooth Connect")
                    .setTicker("Bluetooth")
                    .setContentText("My connections")
                    .setSmallIcon(R.drawable.ic_bluetooth_black_24dp)
//                    .setLargeIcon(
//                            Bitmap.createScaledBitmap(icon, 128, 128, false))
                    .setContentIntent(pendingIntent)
                    .setOngoing(true)
                    .addAction(android.R.drawable.ic_media_play,
                            "Connect", previousPendingIntent).build();
            startForeground(Constants.NOTIFICATION_ID,
                    notification);
        } else if (intent.getAction().equals(Constants.START_CONNECTION_ACTION)) {
            Log.i(LOG_TAG, "Clicked start connection");
        } else if (intent.getAction().equals(
                Constants.STOP_FOREGROUND_ACTION)) {
            Log.i(LOG_TAG, "Received Stop Foreground Intent");
            stopForeground(true);
            stopSelf();
        }
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
