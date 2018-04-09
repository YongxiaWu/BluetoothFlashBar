package com.lab.bluetoothflashbar;
//上传本地Github
import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "BLE_DEBUG";

    private Button btnStartScan;
    private ListView listVIewDevices;

    private BluetoothAdapter bleAdapter;
    private boolean isScanning = false;  // 表示当前是否是扫描状态
    private BluetoothLeScanner scanner;
    private BleScanCallback bleScanCallback;   // 需要持有该callback的引用
    List<BluetoothDevice> devices;
    private DeviceListAdapter listAdapter;
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
        handler = new Handler(){};
        listAdapter = new DeviceListAdapter();
        listVIewDevices.setAdapter(listAdapter);

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

        // 点击按钮后切换扫描和停止扫描状态
        btnStartScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isScanning) {
                    startBleScan();
                    isScanning = true;
                    btnStartScan.setText("停止扫描");
                } else {
                    stopBleScan();
                    isScanning = false;
                    btnStartScan.setText("开始扫描");
                    listAdapter.notifyDataSetChanged();
                }
            }
        });

        // 为listview的每个项设置点击事件
        listVIewDevices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                BluetoothDevice device = devices.get(i);
                Log.i(TAG, "点击了设备：" + device.getName());

                BluetoothGatt gatt = device.connectGatt(MainActivity.this, true, new GattCallbackImpl(), BluetoothDevice.TRANSPORT_LE);
                if(gatt.connect()){// 如果连接成功
                    Utils.currentDevice = device;
                    Utils.gatt = gatt;
                    // 跳转到下一活动
                    Intent intent = new Intent(MainActivity.this, ModifyActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    /**
     * 开启蓝牙设备
     */
    private void startBleScan() {
        final BluetoothLeScanner scanner = bleAdapter.getBluetoothLeScanner();
        bleScanCallback = new BleScanCallback();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                scanner.stopScan(bleScanCallback);
                btnStartScan.setText("开始扫描");
                isScanning = false;
            }
        }, 5000);

        // 开始扫描
        scanner.startScan(bleScanCallback);
    }

    /**
     * 停止扫描
     */
    private void stopBleScan() {
        Log.i(TAG, "停止扫描1：" + scanner);
        Log.i(TAG, "停止扫描2：" + bleAdapter);
        if (scanner != null) {
            scanner.stopScan(bleScanCallback);
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

    private class BleScanCallback extends ScanCallback {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            Log.i(TAG, "扫描到一个设备：" + result.getDevice().getName());

            if (result != null && result.getDevice() != null && result.getDevice().getName() != null) {
                if (devices.contains(result.getDevice())) {
                    Log.i(TAG, "该设备已经扫描过了");
                    return;
                }
                devices.add(result.getDevice());
                listAdapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            Log.i(TAG, "扫描到一堆设备：" + results.size());
            for (ScanResult r : results) {
                if (r.getDevice().getName().startsWith("Flash")) {
                    devices.add(r.getDevice());
                }
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            Log.e(TAG, "扫描失败");
        }
    }

    /**
     * 显示设备列表用的适配器
     */
    private class DeviceListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            if (devices == null) {
                return 0;
            }
            return devices.size();
        }

        @Override
        public Object getItem(int position) {
            if (devices == null || position >= devices.size()) {
                return null;
            }
            return devices.get(position);
        }

        @Override
        public long getItemId(int position) {
            if (devices == null || position >= devices.size()) {
                return -1;
            }
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (devices == null || position >= devices.size()) {
                return null;
            }
            View view = View.inflate(MainActivity.this, R.layout.device_item, null);
            TextView deviceName = (TextView) view.findViewById(R.id.tv_device_name);
            TextView deviceMacAddress = (TextView) view.findViewById(R.id.tv_device_mac);

            BluetoothDevice device = devices.get(position);
            deviceName.setText(device.getName());
            deviceMacAddress.setText(device.getAddress());

            return view;
        }
    }
}
