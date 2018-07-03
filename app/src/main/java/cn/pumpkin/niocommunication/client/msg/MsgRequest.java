package cn.pumpkin.niocommunication.client.msg;

import cn.pumpkin.niocommunication.client.iface.ICompleteListener;
import cn.pumpkin.niocommunication.client.iface.ISendCallBackListener;

public class MsgRequest implements Comparable<MsgRequest>{

	private int req_id; // 命令数据包身份id
	private int priority;// 優先級
	private String body; // 命令消息体

	private int code;//狀態碼
	private boolean isout;// 如果用於查詢超時狀態
	private ICompleteListener<Integer> listener; // 发送是否完成回调
	private ISendCallBackListener<MsgResponse> callbacklistener; // 发送完成后,接受是否成功

	// 超时机制管理
	private long inqueuetime; // 入隊列時間,ms
	private long outqueuetime; // 出隊列時間,ms
	private long timeout;//读写間隔時間(包括多次重发的总时间,所以它要大于time*resendtimedelay),由超时机制进行控制,ms

	// 重发机制管理
	private long sendtime;//发送出去时的时间点
	private long resendtimedelay;//重发时间间隔,如果没有被移除将重发(如何重发: 将超时重发的指令重新添加到正常的发送指令队列中)
	private int totaltime;// 允许重发次数
	private int cnt;// 当前已发送的次数,统计重发次数

	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	public long getInqueuetime() {
		return inqueuetime;
	}
	public void setInqueuetime(long inqueuetime) {
		this.inqueuetime = inqueuetime;
	}
	public long getOutqueuetime() {
		return outqueuetime;
	}
	public void setOutqueuetime(long outqueuetime) {
		this.outqueuetime = outqueuetime;
	}
	public int getTotaltime() {
		return totaltime;
	}
	public void setTotaltime(int totaltime) {
		this.totaltime = totaltime;
	}
	public int getCnt() {
		return cnt;
	}
	public void setCnt(int cnt) {
		this.cnt = cnt;
	}
	public long getSendtime() {
		return sendtime;
	}
	public void setSendtime(long sendtime) {
		this.sendtime = sendtime;
	}
	public long getResendtimedelay() {
		return resendtimedelay;
	}
	public void setResendtimedelay(long resendtimedelay) {
		this.resendtimedelay = resendtimedelay;
	}
	public ISendCallBackListener<MsgResponse> getCallbacklistener() {
		return callbacklistener;
	}
	public void setCallbacklistener(ISendCallBackListener<MsgResponse> callbacklistener) {
		this.callbacklistener = callbacklistener;
	}
	public int getReq_id() {
		return req_id;
	}
	public void setReq_id(int req_id) {
		this.req_id = req_id;
	}
	public boolean isIsout() {
		return isout;
	}
	public void setIsout(boolean isout) {
		this.isout = isout;
	}
	public int getPriority() {
		return priority;
	}
	public void setPriority(int priority) {
		this.priority = priority;
	}
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public long getTimeout() {
		return timeout;
	}
	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}

	public ICompleteListener<Integer> getListener() {
		return listener;
	}
	public void setListener(ICompleteListener<Integer> listener) {
		this.listener = listener;
	}
	@Override
	public int compareTo(MsgRequest msg) {
		// TODO Auto-generated method stub
		return priority>msg.getPriority()?1:0;
	}

}
