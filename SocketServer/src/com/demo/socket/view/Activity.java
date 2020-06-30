package com.demo.socket.view;

import com.demo.socket.model.InterfaceSendMessage;
import com.demo.socket.model.ReceiveDataRunnable;
import com.demo.socket.utils.Constants;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.Objects;

/**
 * ui层
 *
 * @author chenguowu
 * @date 2020/6/18 4:50 PM
 */
public class Activity {
    /**
     * 日志字符串数据
     */
    private String mCurrentLog;
    /**
     * 输入框的控件对象
     */
    private JTextField mInputContent;
    /**
     * 展示提示信息跟客户端在线数量的文本控件
     */
    private JLabel mShowLogText, mConnectUserNum;
    /**
     * 控制层的回调接口对象
     */
    private final InterfaceSendMessage mInterfaceSendMessage;

    public Activity(InterfaceSendMessage socketsManager) {
        this.mInterfaceSendMessage = socketsManager;

        JFrame frame = new JFrame("Socket Demo");
        frame.setSize(450, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //创建面板
        JPanel panel = new JPanel();
        // 添加面板
        frame.add(panel);

        //调用用户定义的方法并添加组件到面板
        placeComponents(panel);

        // 设置界面可见
        frame.setVisible(true);

    }


    /**
     * 为界面增加相关控件
     */
    private void placeComponents(JPanel panel) {

        //这边设置布局为 null
        panel.setLayout(null);

        // 创建 JLabel
        JLabel mCommandHint = new JLabel("Command:");
        mCommandHint.setBounds(10, 20, 80, 25);
        panel.add(mCommandHint);

        //创建文本域用于用户输入
        mInputContent = new JTextField(20);
        mInputContent.setBounds(100, 20, 165, 25);
        panel.add(mInputContent);

        mConnectUserNum = new JLabel("连接用户数：0");
        mConnectUserNum.setVerticalAlignment(JLabel.TOP);
        mConnectUserNum.setBounds(200, 50, 170, 25);
        panel.add(mConnectUserNum);

        mShowLogText = new JLabel("日志");
        mShowLogText.setVerticalAlignment(JLabel.TOP);
        mShowLogText.setBounds(30, 50, 400, 600);
        panel.add(mShowLogText);


        // 创建发送按钮
        JButton mSendButton = new JButton("发送");
        mSendButton.setBounds(310, 20, 80, 25);
        panel.add(mSendButton);
        mSendButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });
    }

    /**
     * 发送消息的点击事件
     */
    public void sendMessage() {
        String sendData = mInputContent.getText().trim();
        if (!"".equals(sendData)) {
            mInterfaceSendMessage.sendMessageCallBack(sendData);
        } else {
            showMessage("请输入内容");
        }
    }

    /**
     * 提示信息的展示
     */
    public void showMessage(String msg) {

        StringBuffer sb = new StringBuffer();

        //拼接文本
        sb.append(Objects.requireNonNullElse(mCurrentLog, "日志"));
        sb.append("<br>");
        sb.append(msg);
        mCurrentLog = sb.toString();

        sb = new StringBuffer();
        sb.append(Constants.HTML_START);
        sb.append(mCurrentLog);
        sb.append(Constants.HTML_END);
        mShowLogText.setText(sb.toString());
    }

    /**
     * 跟新连接数量，有客户端连接上来或者断开的时候会调用此方法
     *
     * {@link ReceiveDataRunnable#run()}
     */
    public void upDateConnectNum() {
        mConnectUserNum.setText("连接用户数：" + mInterfaceSendMessage.getCurrentConnectClientNum());
    }
}
