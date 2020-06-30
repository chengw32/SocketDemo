package com.yqkj.socketdemo.model;

import android.os.Handler;
import android.os.Message;

import com.yqkj.socketdemo.MainActivity;
import com.yqkj.socketdemo.utils.Constants;
import com.yqkj.socketdemo.utils.ThreadPoolUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Random;

/**
 * socket 对象跟 handler 对象的一个管理类
 *
 * <p>在主界面初始化这个管理类 {@link MainActivity#initSocket()}
 *
 * @author chenguowu
 * @date 2020/6/18 6:57 PM
 */
public class SocketClient {

    /**
     * socket对象
     *
     * @author chenguowu
     * @date 2020/6/19 6:22 PM
     */
    private Socket mSocket;
    /**
     * handler对象，用于将从服务器收到数据后发送到主线程展示数据
     *
     * @author chenguowu
     * @date 2020/6/19 6:23 PM
     */
    private Handler mHandler;
    /**
     * 客户端id
     *
     * @author chenguowu
     * @date 2020/6/19 10:28 AM
     */
    private String mClientId;

    public SocketClient(Handler handler) {
        this.mSocket = new Socket();
        this.mHandler = handler;
    }

    /**
     * 连接socket
     *
     * @author chenguowu
     * @date 2020/6/18 12:19 PM
     */
    public void connect() {

        ThreadPoolUtils.executor(new Runnable() {
            @Override
            public void run() {

                //如果关闭了要新建socket对象
                if (mSocket.isClosed()) {
                    mSocket = new Socket();
                }

                try {
                    //如果没连接则需要连接
                    if (!mSocket.isConnected()) {
                        mSocket.connect(new InetSocketAddress(Constants.REMOTE_IP, Constants.REMOTE_PORT));
                        DataOutputStream doc = new DataOutputStream(mSocket.getOutputStream());
                        mClientId = "client" + new Random().nextInt(50);
                        if (mSocket.isConnected()) {
                            doc.writeUTF(mClientId);
                            Message message = new Message();
                            message.obj = "连接成功";
                            mHandler.sendMessage(message);
                        }

                        //接收服务端过来端线程
                        ThreadPoolUtils.executor(new ReceiveDataRecycle(mSocket, mHandler));


                    } else {
                        Message message = new Message();
                        message.obj = "已连接";
                        mHandler.sendMessage(message);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Message message = new Message();
                    message.obj = "连接异常: " + e.getMessage();
                    mHandler.sendMessage(message);
                }
            }
        });
    }


    /**
     * 发送数据到服务器
     *
     * @author chenguowu
     * @date 2020/6/18 10:28 AM
     */
    public void sendData(final String sendData) {
        if (null != mSocket && !mSocket.isClosed()) {

            ThreadPoolUtils.executor(new Runnable() {
                @Override
                public void run() {
                    try {
                        DataOutputStream doc = new DataOutputStream(mSocket.getOutputStream());
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("clientId", mClientId);
                        jsonObject.put("msg", sendData);
                        doc.writeUTF(jsonObject.toString());
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                        Message message = new Message();
                        message.obj = "发送数据异常：" + e.getMessage();
                        mHandler.sendMessage(message);
                    }
                }
            });
        } else {
            Message message = new Message();
            message.obj = "Socket未连接";
            mHandler.sendMessage(message);
        }
    }

    /**
     * 断开连接
     *
     * @author chenguowu
     * @date 2020/6/18 10:29 AM
     */
    public void closeConnect() {

        try {
            mSocket.shutdownOutput();
            mSocket.shutdownInput();
            mSocket.close();
            Message message = new Message();
            message.obj = "已关闭连接";
            mHandler.sendMessage(message);
        } catch (IOException e) {
            e.printStackTrace();
            Message message = new Message();
            message.obj = "关闭异常：" + e.getMessage();
            mHandler.sendMessage(message);
        }
    }


    /**
     * 清空handler队列消息 并置空对象
     *
     * @author chenguowu
     * @date 2020/6/18 12:18 PM
     */
    public void destroy() {
        closeConnect();
        if (null != mHandler) {
            mHandler.removeCallbacksAndMessages(null);
        }
        mHandler = null;
    }

}
