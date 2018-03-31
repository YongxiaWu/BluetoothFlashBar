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
            gatt.discoverServices();   // 连接成功后会开始异步发现服务
        } else {   // 连接失败
            Log.e(TAG, "连接失败");
            Log.e(TAG, "状态："+status+", "+newState);
            discovered = false;
        }
    }

    @Override
    public void onServicesDiscovered(BluetoothGatt gatt, int status) {
        Log.i(TAG, "发现服务:"+gatt.getServices().size());
        BluetoothGattService service = gatt.getService(Utils.generate(Utils.SERVICE_UUID));
        BluetoothGattCharacteristic characteristicWrite = service.getCharacteristic(Utils.generate(Utils.CHARACTERISTIC_WRITE_UUID));
        Utils.characteristicWrite = characteristicWrite;
        Log.i(TAG, "发现写入特征："+characteristicWrite.getUuid().toString());

        BluetoothGattCharacteristic characteristicRead = service.getCharacteristic(Utils.generate(Utils.CHARACTERISTIC_READ_UUID));
        Utils.characteristicRead = characteristicRead;
        Log.i(TAG, "发现读取特征："+characteristicRead.getUuid().toString());

        discovered = true;
    }

    @Override
    public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        Log.i(TAG, "向设备发送信息:"+Arrays.toString(characteristic.getValue())+"状态："+status);

    }

    @Override
    public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        Log.i(TAG, "向设备读取信息:"+Arrays.toString(characteristic.getValue())+"状态："+status);
    }

    @Override
    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        Log.i(TAG, "character状态改变:"+Arrays.toString(characteristic.getValue())+", uuid="+characteristic.getUuid().toString());
    }
}
