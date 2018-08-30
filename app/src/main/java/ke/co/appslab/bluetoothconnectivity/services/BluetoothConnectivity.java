package ke.co.appslab.bluetoothconnectivity.services;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import java.util.Set;

import ke.co.appslab.bluetoothconnectivity.bluetooth.BluetoothConstants;
import ke.co.appslab.bluetoothconnectivity.bluetooth.BluetoothService;

public class BluetoothConnectivity extends Service {
    private static final String TAG = "BluetoothConnectivity";
    private BluetoothAdapter bluetoothAdapter = null;
    private BluetoothService bluetoothService = null;
    String deviceAddress;

    IBinder mBinder = new LocalBinder();

    @Override
    public void onCreate() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothService = new BluetoothService(getApplicationContext(), handler);

        // Get a set of currently paired devices
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        // If there are paired devices, add each one to the ArrayAdapter
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
              deviceAddress  = device.getAddress().substring(device.getAddress().length() - 17);
            }
        }
        if (bluetoothService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (bluetoothService.getState() == BluetoothService.STATE_NONE) {
                // Start the BluetoothConnectivity chat services
                bluetoothService.start();
            }
        }

    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class LocalBinder extends Binder {
        public BluetoothConnectivity getServerInstance() {
            return BluetoothConnectivity.this;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //start a connection
         connect();
        return START_STICKY;// Keeps the service running
    }

    private void connect() {
        // Get the BluetoothDevice object
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (deviceAddress != null){
            BluetoothDevice device = bluetoothAdapter.getRemoteDevice(deviceAddress);
            // Attempt to connect to the device
            bluetoothService.connect(device, false);
        }
    }
    @Override
    public void onDestroy() {
        if (bluetoothService != null) {
            bluetoothService.stop();
        }
    }
    public void printInfo(String printInfo){
        byte[] toPrint = (byte[]) printInfo.getBytes(); // for test purposes
        bluetoothService.write(toPrint);
    }
    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BluetoothConstants.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothService.STATE_CONNECTED:
                            Log.d(TAG, "connected to: " + deviceAddress);
                            break;
                        case BluetoothService.STATE_CONNECTING:
                            Log.d(TAG, "connecting to: " + deviceAddress);
                            break;
                        case BluetoothService.STATE_LISTEN:
                            Log.d(TAG, "listening to: " + deviceAddress);
                            break;
                        case BluetoothService.STATE_NONE:
                            Log.d(TAG, "none connected: ");
                            break;
                    }
                    break;
                case BluetoothConstants.MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    String writeMessage = new String(writeBuf);
                    break;
                case BluetoothConstants.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);

                    break;
                case BluetoothConstants.MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    String connectedDeviceName = msg.getData().getString(BluetoothConstants.DEVICE_NAME);
                    break;
                case BluetoothConstants.MESSAGE_TOAST:
                    Log.d(TAG, msg.getData().getString(BluetoothConstants.TOAST));
                    break;
            }
        }
    };

}
