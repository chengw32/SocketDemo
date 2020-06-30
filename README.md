# SocketDemo
如果要跑通demo
首先: 服务端我是用 intellij idea 开发的。如果用其他软件打跑不起来就用 intellij
其次: 要将手机跟电脑连在同个网络下
最后: Constants的ip地址要填写上电脑的ip地址

实现功能：
客户端连接服务端，客户端发数据到服务端，客户端收到服务端发来的数据
服务端收到客户端发的数据，服务端发数据给客户端

贴部分核心代码

客户端连接服务端：
首先客户端连接服务端必须要在线程里（后面的是 ip地址 跟端口，端口是服务端的socke提供的。）
mSocket.connect(new InetSocketAddress(Constants.REMOTE_IP, Constants.REMOTE_PORT));

发数据：
连接后向服务端发数据。可以发送一个 clientId ，服务端就可以将这个id跟socket对象匹配保存，这样就可以通过id来辨别是哪个客户端。
DataOutputStream doc = new DataOutputStream(mSocket.getOutputStream());
doc.writeUTF(mClientId);//clientid自定义规则

接收数据
用个无限循环等待服务端的数据，必须在线程里
DataInputStream input ;
while (true){       
        input = new DataInputStream(mSocket.getInputStream());
        String data = input.readUTF();
        "接收到服务器数据: " + data;
    } 

这样客户端从连接到发数据，收数据功能都好了。

有个注意点
socket如果关闭（调用mSocket.close()）之后这个客户端对象就没有用了，不能再通过这个对象去connect()，会抛异常，需要重新new一个socket对象连接

服务端：
新建Socket服务，开一个端口，用一个无限循环等待客户端连接过来
try {
    ServerSocket server = new ServerSocket(9991);
    while (true) {
        Socket accept = server.accept();连接上来的客户端对象
        //有客户端链过来的时候 ，开一个线程等待客户端发送过来数据
        ReceiveDataRunnable runnable = new ReceiveDataRunnable(accept, mActivity);
        //添加到容器 ，管理对象
        mRunnableList.add(runnable);
       
        //开始执行线程
        ThreadPoolUtils.execute(runnable);
    }
} 

server.accept()方法只有客户端第一次发消息的时候会触发，同个客户端第二次发消息就不会触发这个方法了，所以要保存 Socket 对象，

收数据：
开一个线程，线程里用一个无限循环去接收数据
while (mCycleFlag) {
    //接收数据
    input = new DataInputStream(mSocketClient.getInputStream());
    String data = input.readUTF();
}

发数据：
用保存起来的socket对象获取输出流，然后写数据即可，写数据需要在线程里执行
DataOutputStream doc = new DataOutputStream(mSocketClient.getOutputStream());
doc.writeUTF(msg);

服务端接收数据，发数据功能都可以了。

具体的看源码吧，小demo而已




