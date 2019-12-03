package com.jellycai.test;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * 重量设置
 */
public class WeightSetActivity extends AppCompatActivity {

    /**
     * 自动获取重量的间隔时间
     */
    public static final String TIME_KEY = "time";

    private EditText etTime;
    private Button btnSave;
    private Button btnBack;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weight_set);
        initView();
    }

    private void initView(){
        etTime = findViewById(R.id.et_time);
        btnSave = findViewById(R.id.btn_save);
        btnBack = findViewById(R.id.btn_back);
        setClickListener();
    }

    private void setClickListener(){
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String timeString = etTime.getText().toString();
                int time = Integer.parseInt(timeString);
                SpUtils.saveValue(WeightSetActivity.this,TIME_KEY,time);
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public static void startActivity(Context context){
        Intent intent = new Intent(context,WeightSetActivity.class);
        context.startActivity(intent);
    }

}
