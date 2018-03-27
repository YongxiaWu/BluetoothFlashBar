package com.lab.bluetoothflashbar;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Arrays;

public class ModifyActivity extends AppCompatActivity {

    private static final String TAG = "MODIFY";

    private EditText editTextInput;
    private CheckBox checkBoxIsChinese;
    private Button btnSend;
    private Button btnBack;
    private TextView tvDisplay;
    private BluetoothGatt gatt;
    private BluetoothDevice device;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify);

        editTextInput = (EditText)findViewById(R.id.et_input);
        checkBoxIsChinese = (CheckBox)findViewById(R.id.cb_is_chinese);
        btnSend = (Button)findViewById(R.id.btn_send);
        btnBack = (Button)findViewById(R.id.btn_back);

        tvDisplay = (TextView)findViewById(R.id.tv_dis);

        gatt = Utils.gatt;
        device = Utils.currentDevice;

        display();

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int i = Integer.valueOf(editTextInput.getText().toString());

            }
        });
    }

    private void send(byte b){
    }

    private void display(){

        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("总的服务数："+gatt.getServices().size());
        for(int i=0; i<gatt.getServices().size(); i++){
            stringBuffer.append("Service "+i);
            BluetoothGattService service = gatt.getServices().get(i);
            for(int j=0; j<service.getCharacteristics().size(); j++){
                stringBuffer.append("    Characteristic "+j);
                BluetoothGattCharacteristic characteristic = service.getCharacteristics().get(j);
                for(int k=0; k<characteristic.getDescriptors().size(); k++){
                    BluetoothGattDescriptor descriptor = characteristic.getDescriptors().get(k);
                    stringBuffer.append("        UUID: "+descriptor.getUuid());
                    stringBuffer.append("        VALUE: "+ Arrays.toString(descriptor.getValue()));
                }
            }
        }

        tvDisplay.setText(stringBuffer.toString());
    }


}
