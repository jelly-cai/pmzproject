package com.jellycai.test;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * 服务器设置Activity
 */
public class SetActivity extends AppCompatActivity {
    /**
     * ip
     */
    public static final String IP_KEY = "IP";
    /**
     * port
     */
    public static final String PORT_KEY = "port";
    /**
     * 是否自动发送
     */
    public static final String IS_AUTO_SEND = "is_auto_send";

    /**
     * 自动获取重量的间隔时间
     */
    public static final String TIME_KEY = "time";

    public static final int DEFAULT_TIME = 1;

    private EditText etIp;
    private EditText etPort;
    private EditText etTime;

    private Button btnSave;
    private Button btnBack;

    private RadioGroup rg;
    private RadioButton rbYes;
    private RadioButton rbNo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set);
        initView();
        initData();
    }

    private void initView() {
        etIp = findViewById(R.id.et_ip);
        etPort = findViewById(R.id.et_port);
        btnSave = findViewById(R.id.btn_save);
        btnBack = findViewById(R.id.btn_back);
        rg = findViewById(R.id.rg);
        rbYes = findViewById(R.id.rb_yes);
        rbNo = findViewById(R.id.rb_no);
        etTime = findViewById(R.id.et_time);
        setOnClickListener();
    }

    private void initData() {
        setInitView();
    }

    private void setInitView() {
        //ip和端口
        String ip = SpUtils.getStringValue(this, IP_KEY);
        int port = SpUtils.getIntegerValue(this, PORT_KEY,0);
        etIp.setText(ip);
        if(port != 0){
            etPort.setText(port + "");
        }
        //是否自动保存
        boolean isAutoSend = SpUtils.getBooleanValue(this, IS_AUTO_SEND,false);
        if (isAutoSend) {
            rbYes.setChecked(true);
        } else {
            rbNo.setChecked(true);
        }
        //获取重量的间隔时间
        String timeString = SpUtils.getIntegerValue(SetActivity.this, TIME_KEY, DEFAULT_TIME) + "";
        etTime.setText(timeString);
    }

    private void setOnClickListener() {
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //保存ip和端口
                String ip = etIp.getText().toString();
                SpUtils.saveValue(SetActivity.this, IP_KEY, ip);
                String port = etPort.getText().toString();
                if(!TextUtils.isEmpty(port)){
                    SpUtils.saveValue(SetActivity.this, PORT_KEY, Integer.parseInt(port));
                }
                //保存是否自动发送
                boolean isAutoSend = getRadioButton();
                SpUtils.saveValue(SetActivity.this, IS_AUTO_SEND, isAutoSend);
                //保存自动获取重量的时间
                String timeString = etTime.getText().toString();
                if(!TextUtils.isEmpty(timeString)){
                    int time = Integer.parseInt(timeString);
                    SpUtils.saveValue(SetActivity.this, TIME_KEY, time);
                }
                Toast.makeText(SetActivity.this,"保存成功",Toast.LENGTH_LONG).show();
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private boolean getRadioButton() {
        int buttonId = rg.getCheckedRadioButtonId();
        if (buttonId == R.id.rb_yes) {
            return true;
        } else if (buttonId == R.id.rb_no) {
            return false;
        }
        return false;
    }

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, SetActivity.class);
        context.startActivity(intent);
    }

}
