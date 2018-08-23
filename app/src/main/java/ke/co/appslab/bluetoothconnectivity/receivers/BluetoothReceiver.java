package ke.co.appslab.bluetoothconnectivity.receivers;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;

import java.util.Set;

import ke.co.appslab.bluetoothconnectivity.bluetooth.BluetoothConstants;
import ke.co.appslab.bluetoothconnectivity.bluetooth.BluetoothService;

public class BluetoothReceiver extends BroadcastReceiver {
    //interface for handling bluetooth connectivity status
    public static ConnectivityReceiverListener connectivityReceiverListener;
    String connectedDeviceName;

    @Override
    public void onReceive(Context context, Intent intent) {
        BluetoothService bluetoothService = new BluetoothService(context, handler);
        BluetoothAdapter  bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        //start bluetooth service
        // Only if the state is STATE_NONE, do we know that we haven't started already
        if (bluetoothService != null && bluetoothService.getState() == BluetoothService.STATE_NONE) {
            // Start the Bluetooth chat services
            bluetoothService.start();

        }
        //get all paired devices
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                connectedDeviceName = device.getAddress();
                //start a secure connection
                // Attempt to connect to the device
                bluetoothService.connect(device, true);
            }
        }

    }

    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BluetoothConstants.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothService.STATE_CONNECTED:
                            connectivityReceiverListener.onBluetoothConnectionChanged(true);
                            break;
                        case BluetoothService.STATE_CONNECTING:
                            connectivityReceiverListener.onBluetoothConnectionChanged(false);
                            break;
                        case BluetoothService.STATE_LISTEN:
                            connectivityReceiverListener.onBluetoothConnectionChanged(false);
                            break;
                        case BluetoothService.STATE_NONE:
                            connectivityReceiverListener.onBluetoothConnectionChanged(false);
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
                    String transactionCost = new String(readBuf, 0, msg.arg1);

                    break;
                case BluetoothConstants.MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    String mConnectedDeviceName = msg.getData().getString(BluetoothConstants.DEVICE_NAME);

                    break;
                case BluetoothConstants.MESSAGE_TOAST:

                    break;
            }
        }
    };
    public interface ConnectivityReceiverListener {
        void onBluetoothConnectionChanged(boolean isConnected);
    }
}
