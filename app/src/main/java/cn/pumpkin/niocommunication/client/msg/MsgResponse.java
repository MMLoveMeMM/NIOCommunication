package cn.pumpkin.niocommunication.client.msg;

public class MsgResponse implements Comparable<MsgResponse>{
	public static final int EVENT_SUCCESS = 0;

    private String method;
    // private MsgType msgType;
    private byte[] rawData;
    private String rawJson;
    private int resultCode = EVENT_SUCCESS;
    private String resultMessage = "";
    private int req_id;
    
	public int getReq_id() {
		return req_id;
	}
	public void setReq_id(int req_id) {
		this.req_id = req_id;
	}
	public String getMethod() {
		return method;
	}
	public void setMethod(String method) {
		this.method = method;
	}
	public byte[] getRawData() {
		return rawData;
	}
	public void setRawData(byte[] rawData) {
		this.rawData = rawData;
	}
	public String getRawJson() {
		return rawJson;
	}
	public void setRawJson(String rawJson) {
		this.rawJson = rawJson;
	}
	public int getResultCode() {
		return resultCode;
	}
	public void setResultCode(int resultCode) {
		this.resultCode = resultCode;
	}
	public String getResultMessage() {
		return resultMessage;
	}
	public void setResultMessage(String resultMessage) {
		this.resultMessage = resultMessage;
	}
	public static int getEventSuccess() {
		return EVENT_SUCCESS;
	}
	@Override
	public int compareTo(MsgResponse o) {
		// TODO Auto-generated method stub
		return 0;
	}
        
}
