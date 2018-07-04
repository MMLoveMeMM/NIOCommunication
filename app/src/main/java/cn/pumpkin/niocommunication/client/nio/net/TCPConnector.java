package cn.pumpkin.niocommunication.client.nio.net;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

public class TCPConnector {

	//管道管理器
	private Selector selector;
	private SocketChannel channel;

	public TCPConnector() {

	}

	public void createLink(String host,int port){
		//获取socket通道
		try {
			channel = SocketChannel.open();
			//获得通道管理器
			selector=Selector.open();

			//客户端连接服务器，需要调用channel.finishConnect();才能实际完成连接。
			channel.connect(new InetSocketAddress(host, port));
			//为该通道注册SelectionKey.OP_CONNECT事件
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/*
     * 这个状态非常重要
     * */
	public boolean isConnect() {
		if(channel!=null && channel.isConnected()) {
			return true;
		}
		return false;
	}

	// 檢查是否連接完成,如果未連接完成,繼續連接完成
	// 不需要這些檢查,對於客戶端沒有太大意義
	public void checkConnect() throws IOException {
		if(channel!=null && channel.isConnected()) {
			System.out.println("finish connected !");
			channel.finishConnect();
		}

	}

	public void sendData(ByteBuffer msg) {

		try {
			channel.write(msg);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	// 註冊管道监听事件类型
	// SelectionKey.OP_CONNECT
	// SelectionKey.OP_ACCEPT
	// SelectionKey.OP_READ
	// SelectionKey.OP_WRITE
	public SelectionKey register(Selector selector,int interestoption) throws ClosedChannelException {
		SelectionKey key = null;
		if(channel!=null) {
			key = channel.register(selector, interestoption);
		}
		return key;
	}

	public SocketChannel getChannel() {
		if(channel!=null) {
			return channel;
		}
		return null;
	}

	public Selector getSelector() {
		return selector;
	}

	public void close() throws IOException {
		channel.close();
	}


}
