package cn.pumpkin.niocommunication.server;

import android.util.Log;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

/**
 * @author: zhibao.Liu
 * @version:
 * @date: 2018/7/3 10:21
 * @des: 启动网络服务端,记得需要加网络权限
 * @see {@link }
 */

public class NIOServer {

    private final static String TAG =NIOServer.class.getName();
    //通道管理器
    private Selector selector;

    public NIOServer(int port){
        try {
            init(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void init(int port) throws IOException {
        Log.d(TAG,"服务器端开始初始化");
        //获取一个ServerSocket通道
        ServerSocketChannel serverChannel = ServerSocketChannel.open();
        serverChannel.configureBlocking(false);
        serverChannel.socket().bind(new InetSocketAddress(port));
        //获取通道管理器
        selector=Selector.open();
        //将通道管理器与通道绑定，并为该通道注册SelectionKey.OP_ACCEPT事件，
        //只有当该事件到达时，Selector.select()会返回，否则一直阻塞。
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);
        Log.d(TAG,"服务器端初始化完成");
        return ;
    }

    public void listen() throws IOException {
        Log.d(TAG,"服务器端启动成功");

        //使用轮询访问selector
        while (true) {
            //当有注册的事件到达时，方法返回，否则阻塞。
            if(selector==null){
                return;
            }
            selector.select();
            Log.d(TAG,"服务器端启动成功 1000");
            //获取selector中的迭代器，选中项为注册的事件
            Iterator<SelectionKey> ite = selector.selectedKeys().iterator();
            boolean isConnect = false;
            Log.d(TAG,"服务器端启动成功 1001");
            while (ite.hasNext()) {
                SelectionKey key = ite.next();
                Log.d(TAG,"服务器端启动成功 1002");
                //删除已选key，防止重复处理
                ite.remove();
                //客户端请求连接事件
                if (key.isAcceptable()) {
                    ServerSocketChannel server = (ServerSocketChannel) key.channel();
                    //获得客户端连接通道
                    SocketChannel channel = server.accept();
                    channel.configureBlocking(false);
                    //向客户端发消息
                    channel.write(ByteBuffer.wrap(new String("send message to client").getBytes()));
                    //在与客户端连接成功后，为客户端通道注册SelectionKey.OP_READ事件。
                    channel.register(selector, SelectionKey.OP_READ);
                    isConnect = true;
                    Log.d(TAG,"客户端请求连接事件");
                } else if (key.isReadable()) {//有可读数据事件
                    //获取客户端传输数据可读取消息通道。
                    SocketChannel channel = (SocketChannel) key.channel();
                    //创建读取数据缓冲器
                    ByteBuffer buffer = ByteBuffer.allocate(100);
                    int read = channel.read(buffer);
                    byte[] data = buffer.array();
                    String message = new String(data);

                    Log.d(TAG,"receive message from client, size:" + buffer.position() + " msg: " + message);
                    /*ByteBuffer outbuffer = ByteBuffer.wrap(("server.".concat(msg)).getBytes());
                    channel.write(outbuffer);*/
                }
                if (isConnect) {
                    ServerSocketChannel server = (ServerSocketChannel) key.channel();
                    //获得客户端连接通道
                    SocketChannel channel = server.accept();
                    channel.write(ByteBuffer.wrap(new String("send message to client").getBytes()));
                }
            }
        }
    }

}
