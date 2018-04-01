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
import android.widget.Toast;

import java.util.Arrays;

public class ModifyActivity extends AppCompatActivity {

    private static final String TAG = "BLE_DEBUG";

    private EditText editTextInput;
    private CheckBox checkBoxIsChinese;
    private Button btnSend;
    private Button btnRead;
    private TextView tvDisplay;


    private BluetoothGatt gatt;
    private BluetoothDevice device;
    private BluetoothGattCharacteristic characteristicWrite;
    private BluetoothGattCharacteristic characteristicRead;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify);

        editTextInput = (EditText)findViewById(R.id.et_input);
        checkBoxIsChinese = (CheckBox)findViewById(R.id.cb_is_chinese);
        btnSend = (Button)findViewById(R.id.btn_send);
        btnRead = (Button)findViewById(R.id.btn_read);

        tvDisplay = (TextView)findViewById(R.id.tv_dis);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = editTextInput.getText().toString();
                boolean isF = "1".equals(s);
                byte[] buffer = new byte[20];
                if(isF){
                    buffer[0] = 1;

                }else{
                    buffer[0] = 2;
                }
                byte i = 0;
                for(i=0; i>=0 && i<=112; i+=16){
                    buffer[1] = i;
                    if(isF){
                        buffer[2] = 0x00;
                        buffer[3] = 98;
                    }else{
                        buffer[2] = 0x00;
                        buffer[3] = 99;
                    }
                    for(int j=4; j<=19; j++){
                        if(isF) {
                            buffer[j] = 0x0f;
                        }else{
                            buffer[j] = 0x0e;
                        }
                    }
                    Log.i(TAG, "开始发送："+Arrays.toString(buffer));
                    send(buffer);
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        btnRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                read();
            }
        });
    }

    private void send(byte[] bytes){

        if(!GattCallbackImpl.discovered){
            Toast.makeText(this, "未连接至蓝牙", Toast.LENGTH_LONG).show();
            return;
        }

        if(gatt==null || device==null || characteristicWrite==null){
            gatt = Utils.gatt;
            device = Utils.currentDevice;
            characteristicWrite = Utils.characteristicWrite;
            characteristicRead = Utils.characteristicRead;
        }

        characteristicWrite.setValue(bytes);
        if(gatt.writeCharacteristic(characteristicWrite)){
            Log.i(TAG, "发送成功");
        }else{
            Log.i(TAG, "发送失败");
        }
    }

    private void read(){
        if(!GattCallbackImpl.discovered){
            Toast.makeText(this, "未连接至蓝牙", Toast.LENGTH_LONG).show();
            return;
        }

        if(gatt==null || device==null || characteristicWrite==null){
            gatt = Utils.gatt;
            device = Utils.currentDevice;
            characteristicWrite = Utils.characteristicWrite;
            characteristicRead = Utils.characteristicRead;
        }

        if(gatt.readCharacteristic(characteristicRead)){
            StringBuilder stringBuilder = new StringBuilder(tvDisplay.getText().toString());
            stringBuilder.append(Arrays.toString(characteristicRead.getValue())+"\n");
            tvDisplay.setText(stringBuilder.toString());
        }else{
            Toast.makeText(this, "读取失败", Toast.LENGTH_LONG).show();
        }
    }

}
