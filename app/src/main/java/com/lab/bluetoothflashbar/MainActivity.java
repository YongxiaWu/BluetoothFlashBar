package com.lab.bluetoothflashbar;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MAIN";

    private Button btnStartScan;
    private ListView listVIewDevices;

    private BluetoothAdapter bleAdapter;
    private boolean isScanning = false;  // 表示当前是否是扫描状态
    private BluetoothLeScanner scanner;
    List<BluetoothDevice> devices;
    private Handler handler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 活动内请求权限
        dynamicRequestPermission(Manifest.permission.BLUETOOTH);
        dynamicRequestPermission(Manifest.permission.BLUETOOTH_ADMIN);
        dynamicRequestPermission(Manifest.permission.ACCESS_COARSE_LOCATION);

        btnStartScan = (Button) findViewById(R.id.btn_start_scan);
        listVIewDevices = (ListView) findViewById(R.id.listview_ble_devices);

        devices = new ArrayList<>();
        handler = new Handler();

        // 判断当前设备是否支持BLE蓝牙
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "当前设备不支持BLE，程序即将退出", Toast.LENGTH_LONG).show();
            finish();
        }

        // 获取蓝牙的适配器
        bleAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bleAdapter == null) {
            Log.e(TAG, "无法获取蓝牙适配器");
            return;
        }

        // 如果未开启蓝牙，将请求开启蓝牙
        if (!bleAdapter.isEnabled()) {
            Log.e(TAG, "未打开蓝牙");
            // TODO 请求开启蓝牙
            return;
        }

        scanner = bleAdapter.getBluetoothLeScanner();

        btnStartScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isScanning){
                    startBleScan();
                    isScanning = true;
                }else{
                    stopBleScan();
                    isScanning = false;
                }
            }
        });



    }

    /**
     * 开启蓝牙设备
     */
    private void startBleScan() {
        final BluetoothLeScanner scanner = bleAdapter.getBluetoothLeScanner();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                scanner.stopScan(new BleScanCallback());
            }
        }, 5000);

        // 开始扫描
        scanner.startScan(new BleScanCallback());
    }

    /**
     * 停止扫描
     */
    private void stopBleScan(){
        if(scanner!=null && bleAdapter!=null && bleAdapter.isDiscovering()){
            scanner.stopScan(new BleScanCallback());
        }
    }

    /**
     * 动态请求权限
     *
     * @param permission
     */
    private void dynamicRequestPermission(String permission) {
        if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{permission}, 0);
        }
    }

    private class BleScanCallback extends ScanCallback{
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            Log.i(TAG, "扫描到一个设备："+result.getDevice().getName());
            if(result.getDevice().getName().startsWith("Flash")) {
                devices.add(result.getDevice());
            }
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            Log.i(TAG, "扫描到一堆设备："+results.size());
            for(ScanResult r:results){
                if(r.getDevice().getName().startsWith("Flash")) {
                    devices.add(r.getDevice());
                }
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            Log.e(TAG, "扫描失败");
        }
    }
}
