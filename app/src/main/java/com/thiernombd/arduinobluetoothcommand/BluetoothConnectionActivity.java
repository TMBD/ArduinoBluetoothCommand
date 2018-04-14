package com.thiernombd.arduinobluetoothcommand;


import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


/**
 * Created by Thierno_M_B_DIALLO on 08/03/2018.
 */

public class BluetoothConnectionActivity extends AppCompatActivity {

    /*private class Receiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                Log.i("MAY APPPPPPPPPPPPPPPP !","RECEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEVER");
                Toast.makeText(BluetoothConnectionActivity.this, "The receiver that means you detected a new bluetooth device!!!", Toast.LENGTH_LONG).show();
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                String deviceName = device.getName();
                String deviceMAC = device.getAddress(); // MAC address
                String deviceClass = device.getBluetoothClass().toString();
                _BluetoothDevice myBtDevice = new _BluetoothDevice(deviceName, deviceMAC, deviceClass, device);
                discoveredDevicesAdapter.addDeviceIn_rv(1, myBtDevice);

            } else Toast.makeText(BluetoothConnectionActivity.this, "blabalbaablabla", Toast.LENGTH_LONG).show();
        }
    }*/

    //private BroadcastReceiver mReceiver;
    private boolean enableBluetooth;
    private static final int DISCOVERING_DURATION = 60*2;
    private static final int REQUEST_DISCOVERING_BT = 3;

    private static final int MY_PERMISSIONS_REQUEST_BT = 1;


    public static final int  MESSAGE_NOT_CONNECTED = 4;
    public static final int  MESSAGE_CONNECTED = 5;


    private BluetoothAdapter mBluetoothAdapter = null;

    BluetoothConnectionAdapter pairedDevicesAdapter;
    BluetoothConnectionAdapter discoveredDevicesAdapter;
    List<_BluetoothDevice> listPairedDevices = new ArrayList<>();
    List<_BluetoothDevice> listDiscoveredDevices = new ArrayList<>();


    public Handler connectionHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == MESSAGE_CONNECTED){
                Toast.makeText(BluetoothConnectionActivity.this, R.string.ms_connection_success, Toast.LENGTH_SHORT).show();
            }
            else if(msg.what == MESSAGE_NOT_CONNECTED){
                Toast.makeText(BluetoothConnectionActivity.this, R.string.ms_faild_to_connect, Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_connection);
        setTitle(R.string.title_bluetooth_connection);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();



////////////////////////////////////////////////////////////////////////////////////////////////////

        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_REQUEST_BT);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
        /*else {
            // Permission has already been granted
            Toast.makeText(this, "Garantie", Toast.LENGTH_SHORT);
        }*/


////////////////////////////////////////////////////////////////////////////////////////////////////


        if (pairedDevices.size() > 0) {
            // There are paired devices. Get the name and address of each paired device.
            for (BluetoothDevice device : pairedDevices) {
                String deviceName = device.getName();
                String deviceMAC = device.getAddress(); // MAC address
                String deviceClass = device.getBluetoothClass().toString();
                listPairedDevices.add(new _BluetoothDevice(deviceName, deviceMAC, deviceClass, device));
            }
        }

        ///////////////////////////////////////////////////////////////////////////

        LinearLayoutManager layoutPairedDevice = new LinearLayoutManager(this);
        layoutPairedDevice.setOrientation(LinearLayoutManager.VERTICAL);

        final RecyclerView pairedDevices_rv = (RecyclerView) findViewById(R.id.pairedDevices_rv);
        pairedDevices_rv.setLayoutManager(layoutPairedDevice);

        //Chargement des Appareils deja apparié dans le rv
        pairedDevicesAdapter = new BluetoothConnectionAdapter(listPairedDevices, true, connectionHandler);
        pairedDevices_rv.setAdapter(pairedDevicesAdapter);
        //---------------------------------------------------------------------------
        LinearLayoutManager layoutDiscoveredDevice = new LinearLayoutManager(this);
        layoutDiscoveredDevice.setOrientation(LinearLayoutManager.VERTICAL);

        final RecyclerView discoveredDevices_rv = (RecyclerView) findViewById(R.id.discoveredDevices_rv);
        discoveredDevices_rv.setLayoutManager(layoutDiscoveredDevice);

        //Chargement des Appareils decouverts dans le rv
        discoveredDevicesAdapter = new BluetoothConnectionAdapter(listDiscoveredDevices, false, connectionHandler);
        discoveredDevices_rv.setAdapter(discoveredDevicesAdapter);

        /////////////////////////////////////////////////////////////////////////////////////////////

        ///////////////////////BROADCAST FOR DEVICE DISCOVERING//////////////////////////////////////

        //IntentFilter hedsetIF = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(mReceiver, filter);




    }//END OF ONCREATE





/////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_bluetooth_connection, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {

            if(!mBluetoothAdapter.isDiscovering()){
                discoveredDevicesAdapter.removeAllDeviceIn_rv();
                Intent discoverableIntent =
                        new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, DISCOVERING_DURATION);
                startActivityForResult(discoverableIntent, REQUEST_DISCOVERING_BT);
            }else {
                enableBluetooth = true;
                if(mBluetoothAdapter.startDiscovery())
                    Toast.makeText(BluetoothConnectionActivity.this, R.string.ms_discovery_started, Toast.LENGTH_SHORT).show();
            }

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_DISCOVERING_BT){
            if(resultCode == DISCOVERING_DURATION){
                enableBluetooth = true;
                if(mBluetoothAdapter.startDiscovery())
                    Toast.makeText(BluetoothConnectionActivity.this, R.string.ms_can_see_near_devices, Toast.LENGTH_LONG).show();
            }else {
                enableBluetooth = false;
                Toast.makeText(BluetoothConnectionActivity.this, R.string.ms_invite_to_active_discovery, Toast.LENGTH_LONG).show();
            }
        }
    }



    @Override
    public void onResume() {
        super.onResume();
        //registerReceiver(mReceiver, filter);
        //MainActivity.mBluetoothAdapter.startDiscovery();
    }

    /*@Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
        mBluetoothAdapter.cancelDiscovery();
    }*/

    private final BroadcastReceiver mReceiver= new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                Log.i("MAY APPPPPPPPPPPPPPPP !","RECEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEVER");
                //Toast.makeText(BluetoothConnectionActivity.this, "Detected a new bluetooth device!!!", Toast.LENGTH_SHORT).show();
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                String deviceName = device.getName();
                String deviceMAC = device.getAddress(); // MAC address
                String deviceClass = device.getBluetoothClass().toString();
                _BluetoothDevice myBtDevice = new _BluetoothDevice(deviceName, deviceMAC, deviceClass, device);
                discoveredDevicesAdapter.addDeviceIn_rv(0, myBtDevice);

            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unregisterReceiver(mReceiver);
    }



    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_BT: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Toast.makeText(BluetoothConnectionActivity.this, R.string.ms_wellcome_after_grant_bt_permission, Toast.LENGTH_LONG).show();

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                    Toast.makeText(BluetoothConnectionActivity.this, R.string.ms_bt_permission_not_granted, Toast.LENGTH_LONG).show();
                }
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

}
