package com.lab.bluetoothflashbar;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;

import java.util.UUID;

/**
 * Created by ifish on 2018/3/27.
 */

public class Utils {

    public static volatile BluetoothDevice currentDevice;
    public static volatile BluetoothGatt gatt;
    public static volatile BluetoothGattCharacteristic characteristic;

    public static final String SERVICE_UUID = "fff0";
    public static final String CHARACTERISTIC_UUID = "fff3";

    public static UUID generate(String chid){
        String s = "0000"+chid+"-0000-1000-8000-00805f9b34fb";
        return UUID.fromString(s.toLowerCase());
    }

}
