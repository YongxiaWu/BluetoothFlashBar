package com.lab.bluetoothflashbar;


import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.util.Log;

import java.util.Arrays;

/**
 * Created by ifish on 2018/3/27.
 */

public class GattCallbackImpl extends BluetoothGattCallback {

    private static final String TAG = "BLE_DEBUG";

    public static volatile boolean discovered = false;

    @Override
    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
        if (newState == BluetoothGatt.STATE_CONNECTED) {   // 连接成功
            Log.i(TAG, "连接成功");
            gatt.discoverServices();   // 则去搜索设备的服务(Service)和服务对应Characteristic
        } else {   // 连接失败
            Log.e(TAG, "连接失败");
            Log.e(TAG, "状态："+status);
        }
    }

    @Override
    public void onServicesDiscovered(BluetoothGatt gatt, int status) {
        BluetoothGattService service = gatt.getService(Utils.generate(Utils.SERVICE_UUID));
        BluetoothGattCharacteristic characteristic = service.getCharacteristic(Utils.generate(Utils.CHARACTERISTIC_UUID));
        Utils.characteristic = characteristic;
        discovered = true;
    }

    @Override
    public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        Log.i(TAG, "向设备发送信息:"+Arrays.toString(characteristic.getValue()));
        Log.i(TAG, "状态："+Integer.toHexString(status));
    }

    @Override
    public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        Log.i(TAG, "向设备读取信息:"+Arrays.toString(characteristic.getValue()));
        Log.i(TAG, "状态："+status);
    }

    @Override
    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        Log.i(TAG, "character状态改变:"+Arrays.toString(characteristic.getValue()));
    }
}
