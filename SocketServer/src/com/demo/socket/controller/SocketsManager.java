package com.demo.socket.controller;

import com.demo.socket.model.InterfaceSendMessage;
import com.demo.socket.model.ReceiveDataRunnable;
import com.demo.socket.utils.Constants;
import com.demo.socket.utils.ThreadPoolUtils;
import com.demo.socket.view.Activity;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * socket 与ui对象端管理类
 *
 * @author chenguowu
 * @date 2020/6/18 6:57 PM
 */
public class SocketsManager implements InterfaceSendMessage {
    /**
     * 操作界面的对象
     */
    private final Activity mActivity;
    /**
     * 打断无限循环的变量
     */
    private boolean mBrokenRecycle = true;

    /**
     * 存放每个连接的客户端的容器，客户端断开后会从这个容器移除
     *
     * {@link #currentConnectClientNum()}
     */
    private final ArrayList<ReceiveDataRunnable> mRunnableList = new ArrayList<>();

    public SocketsManager() {
        this.mActivity = new Activity(this);
        initSocketServer();
    }

    /**
     * 初始化socket服务。等待客户端连接上来
     */
    private void initSocketServer() {
        try {
            ServerSocket server = new ServerSocket(9991);
            while (mBrokenRecycle) {
                Socket accept = server.accept();
                //有客户端链过来的时候 ，开一个线程与之通信
                ReceiveDataRunnable runnable = new ReceiveDataRunnable(accept, mActivity);
                //添加到容器 ，管理对象
                mRunnableList.add(runnable);
                mActivity.upDateConnectNum();
                //开始执行线程
                ThreadPoolUtils.execute(runnable);
            }
        } catch (IOException e) {
            mBrokenRecycle = false;
            e.printStackTrace();
        }
    }


    /**
     * 向客户端发送数据
     *
     * @param msg 要发送的数据
     */
    public void sendMessage(String msg) {
        if (mRunnableList.size() == 0) {
            mActivity.showMessage("没有连接的客户端");
            return;
        }
        //与客户端约定端 clientId 命名规则
        String clientStr = "client";

        // 如果是发给指定定客户端 这个变量会被赋值
        String currentClientId = "";

        //要发送的消息数据，如果包含指令的时候需要去除指令
        String sendMessage = msg;

        //先判断是否群发 符合指令格式才能发送成功 不让提示错误
        if (sendMessage.contains(Constants.COMMAND_ALL)) {
            //如果包含群发的指令，则提取出消息内容
            int i = sendMessage.indexOf(Constants.COMMAND_ALL);
            System.out.println("send --- COMMAND_all i：" + i);
            //包含指令 ： 要去除指令 获取要发送的数据
            String substring = sendMessage.substring(i + Constants.COMMAND_ALL.length()).trim();
            if (!"".equals(substring)) {
                sendMessage = substring;
            } else {
                mActivity.showMessage("输入格式错误");
                return;
            }
        } else if (sendMessage.contains(Constants.COMMAND_ONE)) {

            int i = sendMessage.indexOf(Constants.COMMAND_ONE);
            //除去指令后的字符串
            String message = sendMessage.substring(i + Constants.COMMAND_ONE.length());

            //判断是否包含 client 字符 。包含就截取出 clienid 跟 消息 。不包含就提示格式错误
            if (message.contains(clientStr)) {
                int i1 = message.indexOf(" ");
                if (i1 >= 0) {
                    //按指令格式 首个空格隔开的是clientid
                    String clientId = message.substring(0, i1);

                    //判断是否符合客户端id格式
                    if (clientId.contains(clientStr)) {
                        currentClientId = clientId;
                        sendMessage = message.substring((i1 + 1));
                    } else {
                        mActivity.showMessage("输入格式错误");
                        return;
                    }
                } else {
                    mActivity.showMessage("输入格式错误");
                    return;
                }
            } else {
                //包含指令，但是没有clientid 提示错误
                mActivity.showMessage("输入格式错误");
                return;
            }
        } else {
            mActivity.showMessage("输入格式错误");
            return;
        }

        sendMessageToClient(currentClientId, sendMessage);

    }

    /**
     * 发送数据到客户端
     *
     * <p>如果 currentClientId 不为空说明是发给指定客户端的做循环匹配，如果为空，说明是群发的
     *
     * @param currentClientId 客户端id
     * @param sendMessage     发送端消息数据
     * @author chenguowu
     * @date 2020/6/18 6:57 PM
     */
    private void sendMessageToClient(String currentClientId, String sendMessage) {
        //是否没有匹配到客户端id，默认是true。如果有向客户端发送消息，则会切换成false
        boolean isEmptyClient = true;
        if ("".equals(currentClientId)) {
            //群发
            for (ReceiveDataRunnable receiveDataRunnable : mRunnableList) {
                isEmptyClient = false;
                receiveDataRunnable.sendData(sendMessage);
            }
        } else {
            //发给指定的客户端
            for (ReceiveDataRunnable receiveDataRunnable : mRunnableList) {
                if (currentClientId.equals(receiveDataRunnable.getmClientId())) {
                    isEmptyClient = false;
                    receiveDataRunnable.sendData(sendMessage);
                }
            }
        }

        //如果没有发过消息，说明没有匹配到客户端，做一个提示
        if (isEmptyClient) {
            mActivity.showMessage("未匹配到：" + currentClientId + " 的客户端");
        }

    }

    /**
     * 获取当前连接端客户端数量
     */
    public int currentConnectClientNum() {
        for (int i = 0; i < mRunnableList.size(); i++) {
            ReceiveDataRunnable receiveDataRunnable = mRunnableList.get(i);

            //如果连接已经断开 则移除runnable
            if (receiveDataRunnable.getmSocketClient().isClosed()) {
                mRunnableList.remove(receiveDataRunnable);
            }
        }
        return mRunnableList.size();
    }

    /**
     * ui层点击发送消息的时候回调这个方法
     *
     * {@link Activity#sendMessage()}
     *
     * @param msg 消息内容
     */
    @Override
    public void sendMessageCallBack(String msg) {
        sendMessage(msg);
    }

    /**
     * 获取当前 客户端的连接数量
     *
     * {@link Activity#upDateConnectNum()}
     *
     * @return 客户端连接数
     */
    @Override
    public int getCurrentConnectClientNum() {
        return currentConnectClientNum();
    }
}
