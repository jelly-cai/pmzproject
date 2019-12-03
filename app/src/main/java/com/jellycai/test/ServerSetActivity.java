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

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * 服务器设置Activity
 */
public class ServerSetActivity extends AppCompatActivity {
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

    private EditText etIp;
    private EditText etPort;

    private Button btnSave;
    private Button btnBack;

    private RadioGroup rg;
    private RadioButton rbYes;
    private RadioButton rbNo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_set);
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
        setOnClickListener();
    }

    private void initData() {
        setInitView();
    }

    private void setInitView() {
        String ip = SpUtils.getStringValue(this, IP_KEY);
        int port = SpUtils.getIntegerValue(this, PORT_KEY);
        etIp.setText(ip);
        if(port != 0){
            etPort.setText(port + "");
        }
        boolean isAutoSend = SpUtils.getBooleanValue(this, IS_AUTO_SEND);
        if (isAutoSend) {
            rbYes.setChecked(true);
        } else {
            rbNo.setChecked(true);
        }
    }

    private void setOnClickListener() {
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ip = etIp.getText().toString();
                SpUtils.saveValue(ServerSetActivity.this, IP_KEY, ip);
                String port = etPort.getText().toString();
                if(!TextUtils.isEmpty(port)){
                    SpUtils.saveValue(ServerSetActivity.this, PORT_KEY, Integer.parseInt(port));
                }
                boolean isAutoSend = getRadioButton();
                SpUtils.saveValue(ServerSetActivity.this, IS_AUTO_SEND, isAutoSend);
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
        Intent intent = new Intent(context, ServerSetActivity.class);
        context.startActivity(intent);
    }

}
