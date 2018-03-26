package com.lab.bluetoothflashbar;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MAIN";

    private TextView devices;
    private BluetoothAdapter bleAdapter;
    List<BluetoothDevice> deviceList;
    private Handler handler = new Handler(){

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 活动内请求权限
        dynamicRequestPermission(Manifest.permission.BLUETOOTH);
        dynamicRequestPermission(Manifest.permission.BLUETOOTH_ADMIN);
        dynamicRequestPermission(Manifest.permission.ACCESS_COARSE_LOCATION);

        devices = (TextView)findViewById(R.id.ble_devices);
        deviceList = new ArrayList<>();

        Log.i(TAG, "获取蓝牙适配器");
        if(!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)){
            Log.e(TAG, "无法使用BLE蓝牙1");
            Log.e(TAG, bleAdapter.toString());
            devices.setText("无法使用BLE蓝牙2");
            return;
        }

        // 获取蓝牙的适配器
        bleAdapter = BluetoothAdapter.getDefaultAdapter();
        if(bleAdapter==null){
            Log.e(TAG, "无法使用BLE蓝牙");
            devices.setText("无法使用BLE蓝牙");
            return;
        }

        // 如果未开启蓝牙，将请求开启蓝牙
        if(!bleAdapter.isEnabled()){
            Log.e(TAG, "未打开蓝牙");
            // TODO 请求开启蓝牙
            return;
        }

        final BluetoothLeScanner scanner = bleAdapter.getBluetoothLeScanner();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                scanner.stopScan(new ScanCallback() {
                    @Override
                    public void onScanResult(int callbackType, ScanResult result) {
                        Log.i(TAG, "扫描结束:"+result.getDevice().getName());
                    }

                    @Override
                    public void onBatchScanResults(List<ScanResult> results) {
                        Log.i(TAG, "批量扫描到的个数："+results.size());
                        for(ScanResult r:results) {
                            Log.i(TAG, "批量扫描到的设备："+r.getDevice().getName());
                            deviceList.add(r.getDevice());
                        }
                    }

                    @Override
                    public void onScanFailed(int errorCode) {
                        Log.e(TAG, "扫描失败"+errorCode);
                        devices.setText("扫描失败："+errorCode);
                    }
                });
            }
        }, 5000);

        // 开始扫描
        scanner.startScan(new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                if(result.getDevice().getName()!=null){
                    Log.i(TAG, String.format("扫描到设备：%s, %s", result.getDevice().getName(), result.getDevice().getAddress()));
                }
            }

            @Override
            public void onBatchScanResults(List<ScanResult> results) {
                Log.i(TAG, "批量扫描到的个数："+results.size());
                for(ScanResult r:results) {
                    Log.i(TAG, "批量扫描到的设备："+r.getDevice().getName());
                    deviceList.add(r.getDevice());
                }
            }

            @Override
            public void onScanFailed(int errorCode) {
                Log.e(TAG, "扫描失败"+errorCode);
                devices.setText("扫描失败："+errorCode);
            }
        });

    }

    /**
     * 动态请求权限
     * @param permission
     */
    private void dynamicRequestPermission(String permission){
        if(checkSelfPermission(permission)!= PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{permission}, 0);
        }
    }
}
