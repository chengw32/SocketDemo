package com.demo.socket.model;

import com.demo.socket.utils.ThreadPoolUtils;
import com.demo.socket.view.Activity;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * 用于接收客户端发来的消息
 *
 * @author chenguowu
 * @date 2020/6/18 6:52 PM
 */
public class ReceiveDataRunnable implements Runnable {
    /**
     * Socket对象
     */
    private final Socket mSocketClient;
    /**
     * ui页面对象
     */
    private final Activity mActivity;
    /**
     * 客户端上传上来的id
     */
    private String mClientId = "";
    /**
     * 中断无限循环的flag，与客户端连接断开了这个值会赋值成false 从而中断无限循环
     */
    private boolean mCycleFlag = true;

    public ReceiveDataRunnable(Socket client, Activity window) {
        this.mSocketClient = client;
        this.mActivity = window;
    }

    @Override
    public void run() {
        DataInputStream input;
        try {
            while (mCycleFlag) {
                //接收数据
                input = new DataInputStream(mSocketClient.getInputStream());
                String data = input.readUTF();
                //接收数据
                if ("".equals(mClientId)) {
                    if (data.contains("client")) {
                        mClientId = data;
                        mActivity.showMessage(data + ": 连接成功");
                    } else {
                        //如果不是client则断开连接
                        mSocketClient.shutdownInput();
                        mSocketClient.shutdownOutput();
                        mSocketClient.close();

                        mCycleFlag = false ;
                        //更新ui
                        mActivity.showMessage("非法用户:" + data + " 断开连接");
                        mActivity.upDateConnectNum();
                    }
                } else {
                    mActivity.showMessage("收到数据" + data);
                }

            }

        } catch (IOException e) {

            try {
                //关闭socket
                mSocketClient.shutdownInput();
                mSocketClient.shutdownOutput();
                mSocketClient.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            mCycleFlag = false ;
            //更新ui
            mActivity.showMessage(mClientId + " 断开连接");
            mActivity.upDateConnectNum();
        } catch (Exception e) {
            mActivity.showMessage(e.getMessage());
        }
    }

    /**
     * 发送数据
     *
     * @author chenguowu
     * @date 2020/6/18 5:52 PM
     */
    public void sendData(String msg) {
        if (null != mSocketClient && !mSocketClient.isClosed()) {
            ThreadPoolUtils.execute(() -> {
                try {
                    DataOutputStream doc = new DataOutputStream(mSocketClient.getOutputStream());
                    doc.writeUTF(msg);
                    mActivity.showMessage("发送消息：" + msg + " 给：" + mClientId);

                } catch (IOException e) {
                    mActivity.showMessage("发送消息异常：" + e.getMessage());
                }
            });
        } else {
            mActivity.showMessage("客户端：" + mClientId + " 未连接");
        }
    }

    /**
     * 发送到客户端的时候 需要匹配 clientId
     *
     * @author chenguowu
     * @date 2020/6/18 20:52 PM
     */
    public String getmClientId() {
        return mClientId;
    }

    public Socket getmSocketClient() {
        return mSocketClient;
    }

}
