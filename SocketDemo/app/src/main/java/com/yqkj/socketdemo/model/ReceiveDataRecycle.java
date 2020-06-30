package com.yqkj.socketdemo.model;

import android.os.Handler;
import android.os.Message;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * 连接socket之后 用于接收从服务端发送过来的消息
 *
 * <p>在SocketClient连接成功后开启循环接收数据 {@link SocketClient#connect()}
 *
 * @author chenguowu
 * @date 2020/6/18 6:49 PM
 */
public class ReceiveDataRecycle implements Runnable {

    /**
     * socket对象
     *
     * @author chenguowu
     * @date 2020/6/19 7:55 PM
     */
    private Socket mSocket;
    /**
     * handler对象
     *
     * @author chenguowu
     * @date 2020/6/19 7:59 PM
     */
    private Handler mHandler;
    /**
     * 无限循环等待服务端的数据，但是连接断开的时候需要中断无限循环
     *
     * @author chenguowu
     * @date 2020/6/19 8:00 PM
     */
    private boolean mRecycleFlag = true;

    ReceiveDataRecycle(Socket socket, Handler handler) {
        this.mSocket = socket;
        this.mHandler = handler;
    }

    @Override
    public void run() {

        DataInputStream input;
        while (mRecycleFlag) {
            try {
                Thread.sleep(500);
                input = new DataInputStream(mSocket.getInputStream());
                String data = input.readUTF();
                Message message = new Message();
                message.obj = "接收到服务器数据: " + data;
                mHandler.sendMessage(message);

            } catch (Exception e) {
                Message message = new Message();
                message.obj = "断开连接";
                mHandler.sendMessage(message);

                //断开后关闭socket
                try {
                    mSocket.shutdownInput();
                    mSocket.shutdownOutput();
                    mSocket.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

                mRecycleFlag = false;
            }
        }
    }

}
