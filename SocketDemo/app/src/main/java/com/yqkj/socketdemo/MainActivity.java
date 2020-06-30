package com.yqkj.socketdemo;

import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.yqkj.socketdemo.model.DataCallBack;
import com.yqkj.socketdemo.model.DataReceiveHandler;
import com.yqkj.socketdemo.model.SocketClient;

/**
 * app 主入口类
 *
 * @author chenguowu
 * @date 2020/6/18 6:53 PM
 */
public class MainActivity extends AppCompatActivity implements DataCallBack<String> {

    /**
     * 管理 socket 跟 handler 的类的对象
     */
    private SocketClient mSocketClient;
    /**
     * 展示日志的文本控件
     */
    private TextView mTvData;
    /**
     * 输入框对象，输入发送的内容
     */
    private EditText mEtContent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initUi();
        initSocket();

    }

    /**
     * 初始化socket管理类
     *
     * @author chenguowu
     * @date 2020/6/18 11:40 AM
     */
    private void initSocket() {
        mSocketClient = new SocketClient(new DataReceiveHandler(this));
    }

    /**
     * 初始化控件对象
     *
     * @author chenguowu
     * @date 2020/6/18 12:36 PM
     */
    private void initUi() {
        mEtContent = findViewById(R.id.et_content);
        mTvData = findViewById(R.id.tv_message);
    }

    /**
     * handler接收到数据后 会用接口对象回调该方法从而刷新ui
     *
     * {@link DataReceiveHandler#handleMessage(Message)}
     *
     * @author chenguowu
     * @date 2020/6/19 8:02 PM
     */
    @Override
    public void dataBack(String messageObj) {
        appendMessage(messageObj);
    }

    /**
     * 添加数据展示到文本控件
     *
     * @param msg 展示在textView上的数据
     *
     * @author chenguowu
     * @date 2020/6/18 11:24 AM
     */
    private void appendMessage(String msg) {
        if (null != mTvData) {
            mTvData.append(msg + getString(R.string.next_line));
        }
    }

    /**
     * 连接socket 的点击事件
     *
     * @author chenguowu
     * @date 2020/6/18 10:37 AM
     */
    public void connect(View view) {
        if (null != mSocketClient) {
            mSocketClient.connect();
        }
    }


    /**
     * 断开连接
     *
     * @date 2020/6/18 10:38 AM
     */
    public void disConnect(View view) {
        if (null != mSocketClient) {
            mSocketClient.closeConnect();
        }
    }


    /**
     * 清空handler队列，跟清空对象
     *
     * @author chenguowu
     * @date 2020/6/18 10:59 AM
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mSocketClient != null) {
            mSocketClient.destroy();
        }

    }


    /**
     * 点击事件 向服务器发送数据
     *
     * @author chenguowu
     * @date 2020/6/18 11:16 AM
     */
    public void send(View view) {
        String trim = mEtContent.getText().toString().trim();
        if (!TextUtils.isEmpty(trim)) {
            mSocketClient.sendData(trim);
        } else {
            appendMessage(getString(R.string.input_hint));
        }
    }
}
