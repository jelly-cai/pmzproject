package com.jellycai.test;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.kongqw.serialportlibrary.Device;
import com.kongqw.serialportlibrary.SerialPortFinder;
import com.kongqw.serialportlibrary.SerialPortManager;
import com.kongqw.serialportlibrary.listener.OnSerialPortDataListener;

import java.io.File;
import java.math.BigInteger;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public final String TAG = "MainActivity";

    public static final String RS232_FILE_NAME = "/dev/ttyS4";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btnOk = findViewById(R.id.btn_ok);
        final TextView tvResult = findViewById(R.id.tv_result);
        final SerialPortManager mSerialPortManager = new SerialPortManager();
        mSerialPortManager.setOnSerialPortDataListener(new OnSerialPortDataListener() {
            @Override
            public void onDataReceived(byte[] bytes) {
                final int weight = Rs232Utils.getWeight(StringUtils.byteArrayToHexStr(bytes));
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvResult.setText(weight + "");
                    }
                });
            }


            @Override
            public void onDataSent(byte[] bytes) {
                Log.d(TAG, "onDataSent: " + new String(bytes));
            }
        });
        mSerialPortManager.openSerialPort(new File(RS232_FILE_NAME), 9600);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //发送串口数据
                mSerialPortManager.sendBytes(StringUtils.hexStrToByteArray("01030001000295CB"));
            }

        });
    }
}
