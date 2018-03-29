package com.lab.bluetoothflashbar;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

public class ModifyActivity extends AppCompatActivity {

    private static final String TAG = "BLE_DEBUG";

    private EditText editTextInput;
    private CheckBox checkBoxIsChinese;
    private Button btnSend;
    private Button btnBack;
    private TextView tvDisplay;
    private BluetoothGatt gatt;
    private BluetoothDevice device;
    private BluetoothGattCharacteristic characteristic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify);

        while (!GattCallbackImpl.discovered){
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Log.i(TAG, "等待发现服务中。。。");
        }

        editTextInput = (EditText)findViewById(R.id.et_input);
        checkBoxIsChinese = (CheckBox)findViewById(R.id.cb_is_chinese);
        btnSend = (Button)findViewById(R.id.btn_send);
        btnBack = (Button)findViewById(R.id.btn_back);

        tvDisplay = (TextView)findViewById(R.id.tv_dis);

        gatt = Utils.gatt;
        device = Utils.currentDevice;
        characteristic = Utils.characteristic;

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = editTextInput.getText().toString();
                byte b = Byte.valueOf(s);

//                byte[] bytes = s.getBytes();
                byte[] bytes = new byte[20];
                for(int i=0; i<20; i++){
                    bytes[i] = (byte)(0x30+i);
                }
//                byte[] bytes = {b};
                send(bytes);
            }
        });
    }

    private void send(byte[] bytes){

        characteristic.setValue(bytes);
        if(gatt.writeCharacteristic(characteristic)){
            Log.i(TAG, "发送成功");
        }else{
            Log.i(TAG, "发送失败");
        }
    }

}
