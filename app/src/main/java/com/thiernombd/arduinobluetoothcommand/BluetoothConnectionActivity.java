package com.thiernombd.arduinobluetoothcommand;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
    private boolean enableBluetooth;
    private static final int DISCOVERING_DURATION = 60*2;
    private static final int REQUEST_DISCOVERING_BT = 3;
    IntentFilter filter = null;

    public static BluetoothAdapter mBluetoothAdapter = null;

    BluetoothConnectionAdapter pairedDevicesAdapter;
    BluetoothConnectionAdapter discoveredDevicesAdapter;
    List<_BluetoothDevice> listPairedDevices = new ArrayList<>();
    List<_BluetoothDevice> listDiscoveredDevices = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_connection);
        setTitle("Bluetooth connection");

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

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
        pairedDevicesAdapter = new BluetoothConnectionAdapter(listPairedDevices, true);
        pairedDevices_rv.setAdapter(pairedDevicesAdapter);

        //---------------------------------------------------------------------------
        LinearLayoutManager layoutDiscoveredDevice = new LinearLayoutManager(this);
        layoutDiscoveredDevice.setOrientation(LinearLayoutManager.VERTICAL);

        final RecyclerView discoveredDevices_rv = (RecyclerView) findViewById(R.id.discoveredDevices_rv);
        discoveredDevices_rv.setLayoutManager(layoutDiscoveredDevice);

        //Chargement des Appareils decouverts dans le rv
        discoveredDevicesAdapter = new BluetoothConnectionAdapter(listDiscoveredDevices, true);
        discoveredDevices_rv.setAdapter(discoveredDevicesAdapter);

        /////////////////////////////////////////////////////////////////////////////////////////////

        ///////////////////////BROADCAST FOR DEVICE DISCOVERING/////////////////////////////////////////////
        filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter);





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
                Intent discoverableIntent =
                        new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, DISCOVERING_DURATION);
                startActivityForResult(discoverableIntent, REQUEST_DISCOVERING_BT);
            }else {
                enableBluetooth = true;
                mBluetoothAdapter.startDiscovery();
            }


        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_DISCOVERING_BT){
            if(resultCode == DISCOVERING_DURATION){
                enableBluetooth = true;
                mBluetoothAdapter.startDiscovery();
                Toast.makeText(BluetoothConnectionActivity.this, "You can see now other near devices", Toast.LENGTH_LONG).show();
            }else {
                enableBluetooth = false;
                Toast.makeText(BluetoothConnectionActivity.this, "You couldn't see near devices unless you active you device discovering", Toast.LENGTH_LONG).show();
            }
        }
    }

//////////////////////////////////////////////////////////////////////////////
    // Create a BroadcastReceiver for ACTION_FOUND.
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                Toast.makeText(context, "devicevdidcevsdqvdfbsdgnbg", Toast.LENGTH_LONG).show();
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                String deviceName = device.getName();
                String deviceMAC = device.getAddress(); // MAC address
                String deviceClass = device.getBluetoothClass().toString();
                discoveredDevicesAdapter.addDeviceIn_rv(1, new _BluetoothDevice(deviceName, deviceMAC, deviceClass, device));

            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        registerReceiver(mReceiver, filter);
        //MainActivity.mBluetoothAdapter.startDiscovery();
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
        mBluetoothAdapter.cancelDiscovery();
    }

    /*@Override
    protected void onDestroy() {
        super.onDestroy();
        // Don't forget to unregister the ACTION_FOUND receiver.
        unregisterReceiver(mReceiver);
        //MainActivity.mBluetoothAdapter.cancelDiscovery();
    }*/

}
