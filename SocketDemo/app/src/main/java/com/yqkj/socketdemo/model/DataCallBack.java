package com.yqkj.socketdemo.model;

/**
 * handler接收到数据的时候的,通过这个接口回调到ui，并更新
 *
 * 在主界面的时候实现了该接口{@link com.yqkj.socketdemo.MainActivity#dataBack(String)}
 *
 * @author chenguowu
 * @date 2020/6/18 4:11 PM
 */
public interface DataCallBack<T> {
    /**
     * 回调数据到方法
     *
     * {@link DataReceiveHandler}
     *
     * @param obj handler收到的消息
     *
     * @date 2020/6/18 6:44 PM
     */
    void dataBack(T obj);
}
