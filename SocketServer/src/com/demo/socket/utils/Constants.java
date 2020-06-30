package com.demo.socket.utils;

/**
 * 几个常量
 *
 * {@value #COMMAND_ALL} 发送给全部客户端的指令开头格式
 * {@value #COMMAND_ONE} 发送给指定客户端的指令开头格式
 * {@value #HTML_START} Jlable展示的时候格式拼接的开始符
 * {@value #HTML_END} Jlable展示的时候格式拼接的结束符
 *
 * @author chenguowu
 * @date 2020/6/18 6:52 PM
 */
public class Constants {

    public static final String COMMAND_ALL = "--send-text-to-all ";
    public static final String COMMAND_ONE = "--send-text ";
    public static final String HTML_START = "<html><body>";
    public static final String HTML_END = "</body></html>";

}
