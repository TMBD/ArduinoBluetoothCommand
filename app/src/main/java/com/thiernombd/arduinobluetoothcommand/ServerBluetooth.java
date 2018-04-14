package com.thiernombd.arduinobluetoothcommand;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;


/**
 * Created by Thierno_M_B_DIALLO on 09/03/2018.
 */

public class ServerBluetooth extends Thread {
    private final BluetoothServerSocket mmServerSocket;
    //private BluetoothHandler bluetoothHandler = null;
    private static final UUID MY_UUID = UUID.fromString("8ce255c0-223a-11e0-ac64-0803450c9a66");
    private static final String TAG = "THIERNO_MAMADOU_BOYE";

    private Handler conHandler = null;

    public ServerBluetooth(BluetoothAdapter mBluetoothAdapter, Handler handler) {
        // Use a temporary object that is later assigned to mmServerSocket
        // because mmServerSocket is final.

        conHandler = handler;
        BluetoothServerSocket tmp = null;
        try {
            // MY_UUID is the app's UUID string, also used by the client code.
            tmp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord("Thierno_Mamadou_Boye_SERVER", MY_UUID);
        } catch (IOException e) {
            Log.e(TAG, "EEEEEEEEEECHEEEEEEEEEEERCK Socket's listen() method failed ", e);
        }
        mmServerSocket = tmp;
        //this.bluetoothHandler = bluetoothHandler;
    }

    public void run() {
        Log.d(TAG, "SERVEEEEEEEEEEEEEEEEUR DEMARRE ");
        BluetoothSocket socket = null;
        // Keep listening until exception occurs or a socket is returned.
        while (true) {
            try {
                socket = mmServerSocket.accept();
            } catch (IOException e) {
                Log.e(TAG, "Socket's accept() method failed", e);
                break;
            }

            if (socket != null) {
                // A connection was accepted. Perform work associated with
                // the connection in a separate thread.

                Message message = conHandler.obtainMessage(BluetoothConnectionActivity.MESSAGE_CONNECTED);
                message.sendToTarget();

                //manageMyConnectedSocket(socket);
                Log.d("EEEEEEEYYYYYYYYYYYYYY", "Connection serveur etabliiiiiiiiiiiiiiiit...............................");
                //Intialisation of the socket
                MainActivity.bSocket = socket;
                //Initialisation of the connectedHandler which loop for read from the input stream
                MainActivity.connectedThread  = MainActivity.bluetoothHandler.new ConnectedThread(socket);
                //Now we can loop for read from the inputStream
                MainActivity.connectedThread.start();


                try {
                    mmServerSocket.close();
                } catch (IOException e) {
                    Log.e(TAG, "Could not close the connect socket", e);
                }
                break;
            } else {
                Message message = conHandler.obtainMessage(BluetoothConnectionActivity.MESSAGE_NOT_CONNECTED);
                message.sendToTarget();
            }
        }
    }

    // Closes the connect socket and causes the thread to finish.
    public void cancel() {
        try {
            mmServerSocket.close();
        } catch (IOException e) {
            Log.e(TAG, "Could not close the connect socket", e);
        }
    }

}
