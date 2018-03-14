package com.thiernombd.arduinobluetoothcommand;

import android.bluetooth.BluetoothDevice;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Thierno_M_B_DIALLO on 08/03/2018.
 */

public class _BluetoothDevice {
    public String deviceName;
    public String MAC;
    public String className;
    public BluetoothDevice device;

    public _BluetoothDevice(String deviceName, String MAC, String className, BluetoothDevice device) {
        this.deviceName = deviceName;
        this.MAC = MAC;
        this.className = className;
        this.device = device;
    }

    /*
    public static List<_BluetoothDevice> getTestDevices(){
        List<_BluetoothDevice> list = new ArrayList<_BluetoothDevice>();
        list.add(new _BluetoothDevice("TMBD", "151.546.5454.abce878","Alcatel"));
        list.add(new _BluetoothDevice("MyBluetooth", "157.546.678.abaa78","TECHNO"));
        list.add(new _BluetoothDevice("TMBD", "151.546.5454.abce878","Alcatel"));
        list.add(new _BluetoothDevice("MyBluetooth", "157.546.678.abaa78","TECHNO"));
        list.add(new _BluetoothDevice("TMBD", "151.546.5454.abce878","Alcatel"));
        list.add(new _BluetoothDevice("MyBluetooth", "157.546.678.abaa78","TECHNO"));
        list.add(new _BluetoothDevice("TMBD", "151.546.5454.abce878","Alcatel"));
        list.add(new _BluetoothDevice("MyBluetooth", "157.546.678.abaa78","TECHNO"));
        list.add(new _BluetoothDevice("TMBD", "151.546.5454.abce878","Alcatel"));
        list.add(new _BluetoothDevice("MyBluetooth", "157.546.678.abaa78","TECHNO"));
        return list;
    }*/
}
