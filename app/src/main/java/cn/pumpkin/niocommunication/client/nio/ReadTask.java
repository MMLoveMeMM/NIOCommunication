package cn.pumpkin.niocommunication.client.nio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

import cn.pumpkin.niocommunication.client.constants.Status;
import cn.pumpkin.niocommunication.client.iface.IExceptionListener;
import cn.pumpkin.niocommunication.client.iface.IReadListener;
import cn.pumpkin.niocommunication.client.msg.MsgResponse;
import cn.pumpkin.niocommunication.client.nio.net.TCPConnector;

public class ReadTask implements Runnable{

	private Selector selector;
	private SelectionKey mSelectKey;
	private ByteBuffer mainBuffer;//主缓冲区，用于直接全部读取所有可用内容，包括截断包
	private BlockingQueue<MsgResponse> mReadQueue; // 發送的消息先入隊列
	private TCPConnector mTCPConnector;

	private IReadListener mListener;
	private IExceptionListener mEListener;

	private Thread mReadTaskThread;

	private int count=1000;

	public ReadTask(TCPConnector connector) throws IOException {
		mTCPConnector=connector;
		selector=Selector.open();
		mReadQueue=new PriorityBlockingQueue();
		mainBuffer = ByteBuffer.allocate(64);
	}

	public void registerListener(IReadListener listener0,IExceptionListener listener1) {
		mListener=listener0;
		mEListener=listener1;
	}

	public void readWork(){
		// 开线程,不然会阻塞主线程的
		mReadTaskThread=new Thread(this);
		mReadTaskThread.start();

	}

	public BlockingQueue<MsgResponse> getReadQueue() {
		if(mReadQueue!=null && !mReadQueue.isEmpty()) {
			return mReadQueue;
		}
		return null;
	}

	public void readDataFromChannel(SocketChannel socketChannel, String tcpConnectionKey) {

		try {
			ByteBuffer tempBuffer = null;
			mainBuffer.clear();//重置主缓冲区的下标
			socketChannel.read(mainBuffer);
			mainBuffer.flip();//回绕主缓冲区，转写为读

			byte[] data = mainBuffer.array();
			String message = new String(data);
			if(message!=null && !message.isEmpty()) {
				System.out.println("readdata message : "+message);
				MsgResponse msg=new MsgResponse();
				msg.setMethod("dm_Set");
				msg.setResultCode(0);
				msg.setResultMessage(message);
				msg.setReq_id(1001/*++count*/);
				if(mListener!=null) {
					mListener.onReceiver(msg,0);
				}
				mReadQueue.add(msg);
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			if(mEListener!=null) {
				// 读取异常,设置网络重连,更新状态机
				mEListener.onError(Status.NIO_NO_CONNECTION_EXCEPTION);
			}
			e.printStackTrace();
		}//将通道可读数据全部写入到主缓冲区(catch IOException)

	}

	@Override
	public void run() {
		while(true) {
			readDataFromChannel(mTCPConnector.getChannel(),"");
		}
	}

	public void destroy() {
		if(mReadTaskThread!=null && mReadTaskThread.isAlive()) {
			mReadQueue.clear();
			mReadTaskThread.stop();
			mReadTaskThread=null;
		}
	}


}
