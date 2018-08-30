package ke.co.appslab.bluetoothconnectivity;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import ke.co.appslab.bluetoothconnectivity.services.BluetoothConnectivity;
import ke.co.appslab.bluetoothconnectivity.views.DevicesListActivity;

public class HomeActivity extends AppCompatActivity {
    public BluetoothConnectivity bluetoothConnectivity;
    boolean mBounded;
    int REQUEST_ACCESS_COARSE_LOCATION = 1;
    private static final String TAG = "BluetoothActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //request location permission
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_ACCESS_COARSE_LOCATION);

        //bind bluetooth connectivity service to this activity
        Intent mIntent = new Intent(this, BluetoothConnectivity.class);
        startService(mIntent);
        bindService(mIntent, mConnection, BIND_AUTO_CREATE);


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            //test print
            //make sure mBounded = true as this means the service is already bounded
            if (mBounded){
                String printInfo = "test";
                //call print method from bluetooth connectivity service
                bluetoothConnectivity.printInfo(printInfo);
            }
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        });
    }

    ServiceConnection mConnection = new ServiceConnection() {

        public void onServiceDisconnected(ComponentName name) {
            mBounded = false;
            bluetoothConnectivity = null;
        }

        public void onServiceConnected(ComponentName name, IBinder service) {
            mBounded = true;
            BluetoothConnectivity.LocalBinder mLocalBinder = (BluetoothConnectivity.LocalBinder)service;
            bluetoothConnectivity = mLocalBinder.getServerInstance();


        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            //view a list of paired devices
            Intent devicesListIntent = new Intent(this, DevicesListActivity.class);
            startActivity(devicesListIntent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
    @Override
    protected void onStop() {
        super.onStop();
        if(mBounded) {
            unbindService(mConnection);
            mBounded = false;
        }
    }
}
