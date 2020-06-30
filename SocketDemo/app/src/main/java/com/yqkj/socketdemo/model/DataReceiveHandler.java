package com.yqkj.socketdemo.model;


import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;

import com.yqkj.socketdemo.MainActivity;

/**
 * 用于接收handler分发到消息，并通过接口回调数据
 *
 * <p>在主界面实例化对象 {@link MainActivity#initSocket()}
 *
 * @author chenguowu
 * @date 2020/6/18 6:53 PM
 */
public class DataReceiveHandler extends Handler {

    private DataCallBack<String> mDataCallBack;

    public DataReceiveHandler(DataCallBack<String> mDataCallBack) {
        this.mDataCallBack = mDataCallBack;
    }

    @Override
    public void handleMessage(@NonNull Message msg) {
        super.handleMessage(msg);
        if (null != mDataCallBack) {
            if (null != msg.obj) {
                mDataCallBack.dataBack(msg.obj.toString());
            }
        }
    }
}
