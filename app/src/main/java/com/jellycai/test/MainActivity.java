package com.jellycai.test;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.kongqw.serialportlibrary.SerialPortManager;
import com.kongqw.serialportlibrary.listener.OnSerialPortDataListener;
import com.xuhao.didi.socket.client.sdk.OkSocket;
import com.xuhao.didi.socket.client.sdk.client.ConnectionInfo;
import com.xuhao.didi.socket.client.sdk.client.action.SocketActionAdapter;
import com.xuhao.didi.socket.client.sdk.client.connection.IConnectionManager;

import java.io.File;

import static android.widget.Toast.LENGTH_SHORT;

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

    private int lastWeight;
    private int lastTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initListener();
        initSerialPort();
    }

    private void initView(){
        btnOk = findViewById(R.id.btn_ok);
        btnSend = findViewById(R.id.btn_send);
        btnServerSet = findViewById(R.id.btn_server_set);
        btnWeightSet = findViewById(R.id.btn_weight_set);
        tvResult = findViewById(R.id.tv_result);
    }

    private void initListener(){
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendWeightMessage();
            }
        });
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ip = SpUtils.getStringValue(MainActivity.this, ServerSetActivity.IP_KEY);
                int port = SpUtils.getIntegerValue(MainActivity.this, ServerSetActivity.PORT_KEY);
                if(TextUtils.isEmpty(ip) || port == 0){
                    Toast.makeText(MainActivity.this,"请设置服务器地址和端口",LENGTH_SHORT).show();
                    return;
                }
                //连接参数设置(IP,端口号),这也是一个连接的唯一标识,不同连接,该参数中的两个值至少有其一不一样
                ConnectionInfo info = new ConnectionInfo(ip, port);
                //调用OkSocket,开启这次连接的通道,拿到通道Manager
                IConnectionManager manager = OkSocket.open(info);
                //注册Socket行为监听器,SocketActionAdapter是回调的Simple类,其他回调方法请参阅类文档
                manager.registerReceiver(new SocketActionAdapter(){
                    @Override
                    public void onSocketConnectionSuccess(ConnectionInfo info, String action) {
                        //Toast.makeText(MainActivity.this, "连接成功", LENGTH_SHORT).show();
                    }
                });
                //调用通道进行连接
                manager.connect();
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
     * 初始化端口
     */
    private void initSerialPort(){
        mSerialPortManager = new SerialPortManager();
        mSerialPortManager.setOnSerialPortDataListener(new OnSerialPortDataListener() {
            @Override
            public void onDataReceived(byte[] bytes) {
                //获取重量
                final int weight = Rs232Utils.getWeight(StringUtils.byteArrayToHexStr(bytes));
                //写入主页
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvResult.setText(weight + "");
                    }
                });
                //判断读数是否已经稳定下来

            }

            @Override
            public void onDataSent(byte[] bytes) {
                Log.d(TAG, "onDataSent: " + new String(bytes));
            }
        });
        mSerialPortManager.openSerialPort(new File(RS232_FILE_NAME), 9600);
        startLoopSendWeightMessage();
    }

    private void startLoopSendWeightMessage(){
        sendWeightMessage();
    }

    private void sendWeightMessage(){
        if(mSerialPortManager == null){
            return;
        }
        //发送串口数据
        mSerialPortManager.sendBytes(StringUtils.hexStrToByteArray("01030001000295CB"));
    }

}
