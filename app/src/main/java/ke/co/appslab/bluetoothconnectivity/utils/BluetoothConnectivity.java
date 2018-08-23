package ke.co.appslab.bluetoothconnectivity.utils;

import android.app.Application;

import ke.co.appslab.bluetoothconnectivity.receivers.BluetoothReceiver;

public class BluetoothConnectivity extends Application {
    private static BluetoothConnectivity  instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance=this;
    }

    public static synchronized BluetoothConnectivity getInstance(){
        return instance;
    }
    public void setConnectivityListener(BluetoothReceiver.ConnectivityReceiverListener listener) {
        BluetoothReceiver.connectivityReceiverListener = listener;
    }
}
