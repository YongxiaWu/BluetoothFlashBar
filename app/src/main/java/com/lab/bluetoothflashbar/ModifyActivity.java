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
                String s = editTextNum.getText().toString();
                boolean isF = "1".equals(s);
                byte[] buffer = new byte[20];
                if (isF) {
                    buffer[0] = 1;

                } else {
                    buffer[0] = 2;
                }
                s = editTextInput.getText().toString();
                if (s == null || s.trim().length() == 0 || s.trim().length() > 4) {
                    Toast.makeText(ModifyActivity.this, "请输入长度为1-4的字符串", Toast.LENGTH_LONG).show();
                    return;
                }
                String[] ss = s.trim().split("");
                byte[] buffers = new byte[128];
                int index = 0;
                for (int i=0; i<ss.length; i++) {
                    String s1 = ss[i];
                    byte[] bf1 = Hzk16Uttils.readSingle(s1);
                    if (bf1 == null) {
                        bf1 = AsciiZimoUtils.readSingleAsciiZimo(s1);
                    }
                    if (bf1 == null) {
                        Toast.makeText(ModifyActivity.this, "请输入合法字符", Toast.LENGTH_LONG).show();
                        return;
                    }
                    System.arraycopy(bf1, 0, buffers, index, bf1.length);
                    index+=bf1.length;
                }
                byte[] chas = new byte[8];
                try {
                    byte[] bb = s.trim().getBytes("GB2312");
                    System.arraycopy(bb, 0, chas, 0, bb.length);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                byte i = 0;
                for (i = 0; i >= 0 && i <= 112; i += 16) {
                    int k = i/16;
                    buffer[1] = i;
                    if(k<4) {
                        buffer[2] = chas[2 * i];
                        buffer[3] = chas[2 * i + 1];
                    }
                    System.arraycopy(buffers, i, buffer, 4, 16);
                    Log.i(TAG, "开始发送：" + Arrays.toString(buffer));
                    send(buffer);
                    try {
                        Thread.sleep(1000);
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
