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

import com.yyb.AsciiZimoUtils;
import com.yyb.Hzk16Uttils;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

public class ModifyActivity extends AppCompatActivity {

    private static final String TAG = "BLE_DEBUG";

    private EditText editTextInput;
    private EditText editTextNum;
    private Button btnSend;
    private Button btnRead;
    private Button btnDisconnect;
    private TextView tvDisplay;


    private BluetoothGatt gatt;
    private BluetoothDevice device;
    private BluetoothGattCharacteristic characteristicWrite;
    private BluetoothGattCharacteristic characteristicRead;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify);

        editTextInput = (EditText) findViewById(R.id.et_input);
        editTextNum = (EditText) findViewById(R.id.et_num);
        btnSend = (Button) findViewById(R.id.btn_send);
        btnRead = (Button) findViewById(R.id.btn_read);
        btnDisconnect = (Button) findViewById(R.id.btn_disconnect);

        tvDisplay = (TextView) findViewById(R.id.tv_dis);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = editTextInput.getText().toString().trim();
                String num = editTextNum.getText().toString();
                boolean isFirst = "1".equals(num.trim());
                byte[] zimos = new byte[128];
                int zimoIndex = 0;
                byte[] chas = new byte[8];

                for (int i = 0; i < content.length(); i++) {
                    String word = String.valueOf(content.charAt(i));
                    byte[] zimo = Hzk16Uttils.readSingle(word);
                    if (zimo == null) {
                        // 说明不是汉字
                        chas[2 * i] = 0;
                        chas[2 * i + 1] = (byte) content.charAt(i);
                        zimo = AsciiZimoUtils.readSingleAsciiZimo(word);
                    } else {
                        try {
                            chas[2 * i] = word.getBytes("GB2312")[0];
                            chas[2 * i + 1] = word.getBytes("GB2312")[1];
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }

                    if (zimo == null) {
                        Toast.makeText(ModifyActivity.this, "请输入合法字符", Toast.LENGTH_LONG).show();
                        return;
                    }

                    System.arraycopy(zimo, 0, zimos, zimoIndex, zimo.length);
                    zimoIndex += zimo.length;
                }

                // 发送信息
                byte[] buffer = new byte[20];
                if(isFirst){
                    buffer[0] = 1;
                }else{
                    buffer[0] = 2;
                }
                for(byte i=0; i>=0 && i<=112; i+=16){
                    buffer[1] = i;
                    if(i<=48){
                        buffer[2]=chas[2*i/16];
                        buffer[3]=chas[2*i/16+1];
                    }
                    System.arraycopy(zimos, i, buffer, 4, 16);
                    send(buffer);
                    try {
                        Thread.sleep(2000);
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

    private void send(byte[] bytes) {
        Log.i(TAG, "发送信息："+Arrays.toString(bytes));
        if (!GattCallbackImpl.discovered) {
            Toast.makeText(this, "未连接至蓝牙", Toast.LENGTH_LONG).show();
            return;
        }

        if (gatt == null || device == null || characteristicWrite == null) {
            gatt = Utils.gatt;
            device = Utils.currentDevice;
            characteristicWrite = Utils.characteristicWrite;
            characteristicRead = Utils.characteristicRead;
        }

        characteristicWrite.setValue(bytes);
        if (gatt.writeCharacteristic(characteristicWrite)) {
            Log.i(TAG, "发送成功");
        } else {
            Log.i(TAG, "发送失败");
        }
    }

    private void read() {
        if (!GattCallbackImpl.discovered) {
            Toast.makeText(this, "未连接至蓝牙", Toast.LENGTH_LONG).show();
            return;
        }

        if (gatt == null || device == null || characteristicWrite == null) {
            gatt = Utils.gatt;
            device = Utils.currentDevice;
            characteristicWrite = Utils.characteristicWrite;
            characteristicRead = Utils.characteristicRead;
        }

        if (gatt.readCharacteristic(characteristicRead)) {
            StringBuilder stringBuilder = new StringBuilder(tvDisplay.getText().toString());
            stringBuilder.append(Arrays.toString(characteristicRead.getValue()) + "\n");
            tvDisplay.setText(stringBuilder.toString());
        } else {
            Toast.makeText(this, "读取失败", Toast.LENGTH_LONG).show();
        }
    }

}
