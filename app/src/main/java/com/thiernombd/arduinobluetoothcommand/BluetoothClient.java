package com.thiernombd.arduinobluetoothcommand;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;



/**
 * Created by Thierno_M_B_DIALLO on 09/03/2018.
 */

public class BluetoothClient extends Thread {
    private static final UUID MY_UUID = UUID.fromString("8ce255c0-223a-11e0-ac64-0803450c9a66");
    private final BluetoothSocket mmSocket;
    private final BluetoothDevice mmDevice;
    BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    private Handler conHandler = null;

    private static final String TAG = "THIERNO_MAMADOU_BOYE";

    public BluetoothClient(BluetoothDevice device, Handler handler) {
        // Use a temporary object that is later assigned to mmSocket
        // because mmSocket is final.
        BluetoothSocket tmp = null;
        mmDevice = device;

        conHandler = handler;

        try {
            // Get a BluetoothSocket to connect with the given BluetoothDevice.
            // MY_UUID is the app's UUID string, also used in the server code.
            tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
        } catch (IOException e) {
            Log.e(TAG, "EEEERRRRREEEEEUUUUURRRRR : Socket's create() method failed", e);
        }
        mmSocket = tmp;
    }

    public void run() {
        // Cancel discovery because it otherwise slows down the connection.
        //C'est deja fait dans la classe BluetoothAdapter ; mofifié
        if(mBluetoothAdapter.isDiscovering()) mBluetoothAdapter.cancelDiscovery();

        try {
            // Connect to the remote device through the socket. This call blocks
            // until it succeeds or throws an exception.
            Log.d("......................", "Connection...............................");
            mmSocket.connect();
            Log.d("EEEEEEEYYYYYYYYYYYYYY", "Connectionn client etabliiiiiiiiiiiiiiiit...............................");
        } catch (IOException connectException) {
            // Unable to connect; close the socket and return.
            try {
                Log.d("......................", "Connection ECHOUEE...............................");
                Message message = conHandler.obtainMessage(BluetoothConnectionActivity.MESSAGE_NOT_CONNECTED);
                message.sendToTarget();
                mmSocket.close();
            } catch (IOException closeException) {
                Log.e(TAG, "Could not close the client socket", closeException);
            }
            return;
        }

        // The connection attempt succeeded. Perform work associated with
        // the connection in a separate thread.

        Message message = conHandler.obtainMessage(BluetoothConnectionActivity.MESSAGE_CONNECTED);
        message.sendToTarget();

        //Socket initialisation for the communication
        MainActivity.bSocket = mmSocket;
        //Initialisation of the connectedHandler which loop for read from the input stream
        MainActivity.connectedThread  = MainActivity.bluetoothHandler.new ConnectedThread(mmSocket);
        //Now we can loop for read from the inputStream
        MainActivity.connectedThread.start();
        //manageMyConnectedSocket(mmSocket);
    }

    // Closes the client socket and causes the thread to finish.
    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) {
            Log.e(TAG, "Could not close the client socket", e);
        }
    }

}
