package com.jellycai.test;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.kongqw.serialportlibrary.SerialPortManager;
import com.kongqw.serialportlibrary.listener.OnSerialPortDataListener;
import com.xuhao.didi.core.pojo.OriginalData;
import com.xuhao.didi.socket.client.sdk.OkSocket;
import com.xuhao.didi.socket.client.sdk.client.ConnectionInfo;
import com.xuhao.didi.socket.client.sdk.client.action.SocketActionAdapter;
import com.xuhao.didi.socket.client.sdk.client.connection.IConnectionManager;

import java.io.File;

import static android.widget.Toast.LENGTH_SHORT;
import static com.jellycai.test.WeightSetActivity.DEFAULT_TIME;

/**
 * 主页
 */
public class MainActivity extends AppCompatActivity {

    public final String TAG = "MainActivity";

    public static final String RS232_FILE_NAME = "/dev/ttyS4";

    private SerialPortManager mSerialPortManager;

    private TextView tvResult;
    private Button btnOk;
    private Button btnSend;
    private Button btnServerSet;
    private Button btnWeightSet;

    private int[] weights = new int[3];

    private IConnectionManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initListener();
        initSerialPort();
    }

    private void initView() {
        btnOk = findViewById(R.id.btn_ok);
        btnSend = findViewById(R.id.btn_send);
        btnServerSet = findViewById(R.id.btn_server_set);
        btnWeightSet = findViewById(R.id.btn_weight_set);
        tvResult = findViewById(R.id.tv_result);
    }

    @Override
    protected void onResume() {
        super.onResume();
        boolean isAutoSend = SpUtils.getBooleanValue(MainActivity.this, ServerSetActivity.IS_AUTO_SEND, false);
        if(isAutoSend){
            btnSend.setVisibility(View.GONE);
        }else{
            btnSend.setVisibility(View.VISIBLE);
        }
    }

    private void initListener() {
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendWeightMessage();
            }
        });
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int weight = getWeightForWeights();
                if(weight != 0){
                    sendWeight(weight);
                }else{
                    Toast.makeText(MainActivity.this,"没有重量读数", LENGTH_SHORT).show();
                }
            }
        });
        btnServerSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ServerSetActivity.startActivity(MainActivity.this);
            }
        });
        btnWeightSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WeightSetActivity.startActivity(MainActivity.this);
            }
        });
    }

    /**
     * 添加重量到数组
     *
     * @param weight
     */
    private void addWeight(int weight) {
        boolean isAdd = false;
        for (int i = 0; i < weights.length; i++) {
            if (weights[i] == 0) {
                weights[i] = weight;
                isAdd = true;
                break;
            }
        }
        if (!isAdd) {
            for (int i = 1; i < weights.length; i++) {
                weights[i - 1] = weights[i];
            }
            weights[weights.length - 1] = weight;
        }
    }

    /**
     * 读数是否稳定下来
     *
     * @return
     */
    private boolean isStable() {
        int w = weights[0];
        for (int i = 1; i < weights.length; i++) {
            if (w != weights[i]) {
                return false;
            }
        }
        return true;
    }

    /**
     * 从数组中获得最新重量
     * @return
     */
    private int getWeightForWeights() {
        for (int i = weights.length - 1; i >= 0; i--) {
            if(weights[i] != 0){
                return weights[i];
            }
        }
        return 0;
    }

    /**
     * 初始化端口
     */
    private void initSerialPort() {
        mSerialPortManager = new SerialPortManager();
        mSerialPortManager.setOnSerialPortDataListener(new OnSerialPortDataListener() {
            @Override
            public void onDataReceived(byte[] bytes) {
                //获取重量
                final int weight = Rs232Utils.getWeight(StringUtils.byteArrayToHexStr(bytes));
                if (weight <= 0) {
                    return;
                }
                //写入主页
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvResult.setText(weight + "");
                    }
                });
                //判断读数是否已经稳定下来
                addWeight(weight);
                boolean isAutoSend = SpUtils.getBooleanValue(MainActivity.this, ServerSetActivity.IS_AUTO_SEND, false);
                if (isAutoSend && isStable() && weights[0] != 0) {
                    stopLoopSendGetWeightMessage();
                    sendWeight(weight);
                }
            }

            @Override
            public void onDataSent(byte[] bytes) {
                Log.d(TAG, "onDataSent: " + new String(bytes));
            }
        });
        mSerialPortManager.openSerialPort(new File(RS232_FILE_NAME), 9600);
        startLoopSendGetWeightMessage();
    }

    private void sendWeight(int weight) {
        String ip = SpUtils.getStringValue(MainActivity.this, ServerSetActivity.IP_KEY);
        int port = SpUtils.getIntegerValue(MainActivity.this, ServerSetActivity.PORT_KEY, 0);
        if (TextUtils.isEmpty(ip) || port == 0) {
            Toast.makeText(MainActivity.this, "请设置服务器地址和端口", LENGTH_SHORT).show();
            return;
        }
        //连接参数设置(IP,端口号),这也是一个连接的唯一标识,不同连接,该参数中的两个值至少有其一不一样
        ConnectionInfo info = new ConnectionInfo(ip, port);
        //调用Socket,开启这次连接的通道,拿到通道Manager
        manager = OkSocket.open(info);
        //注册Socket行为监听器,SocketActionAdapter是回调的Simple类,其他回调方法请参阅类文档
        manager.registerReceiver(new SocketActionAdapter() {
            @Override
            public void onSocketConnectionSuccess(ConnectionInfo info, String action) {
                Toast.makeText(MainActivity.this, "连接成功", LENGTH_SHORT).show();
            }

            @Override
            public void onSocketReadResponse(ConnectionInfo info, String action, OriginalData data) {
                super.onSocketReadResponse(info, action, data);
                String response = new String(data.getBodyBytes());
                Toast.makeText(MainActivity.this, response, LENGTH_SHORT).show();
                handler.removeCallbacks(sendWeightTask);
                startLoopSendGetWeightMessage();
            }
        });
        //调用通道进行连接
        manager.connect();
        manager.send(new SendWeightData(weight));
        handler.postDelayed(sendWeightTask,20 * 1000);
    }



    /**
     * 开启循环获取重量
     */
    private void startLoopSendGetWeightMessage() {
        int time = SpUtils.getIntegerValue(MainActivity.this, WeightSetActivity.TIME_KEY, DEFAULT_TIME);
        handler.postDelayed(sendGetWeightTask, time * 1000);
    }

    /**
     * 关闭循环获取重量
     */
    private void stopLoopSendGetWeightMessage(){
        handler.removeCallbacks(sendGetWeightTask);
    }

    private void sendWeightMessage() {
        if (mSerialPortManager == null) {
            return;
        }
        //发送串口数据
        mSerialPortManager.sendBytes(StringUtils.hexStrToByteArray("01030001000295CB"));
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            if (msg.what == 1) {
                sendWeightMessage();
            }
        }
    };

    private Runnable sendGetWeightTask = new Runnable() {
        @Override
        public void run() {
            handler.sendEmptyMessage(1);
            int time = SpUtils.getIntegerValue(MainActivity.this, WeightSetActivity.TIME_KEY, DEFAULT_TIME);
            handler.postDelayed(this, time * 1000);
        }
    };

    private Runnable sendWeightTask = new Runnable() {
        @Override
        public void run() {
            if(manager != null){
                manager.disconnect();
                manager = null;
            }
            sendWeight(getWeightForWeights());
        }
    };

}
