package com.demo.socket.model;

import com.demo.socket.controller.SocketsManager;

/**
 * ui层相关操作的一个回调
 *
 * 在 {@link SocketsManager} 实现该接口
 *
 * @author chenguowu
 */
public interface InterfaceSendMessage {
    /**
     * 发送消息回调
     *
     * @param msg 消息内容
     */
    void sendMessageCallBack(String msg);

    /**
     * 获取当前连接的客户端数量
     *
     * {@link SocketsManager#getCurrentConnectClientNum()}
     *
     * @return 客户端数量
     */
    int getCurrentConnectClientNum();
}
