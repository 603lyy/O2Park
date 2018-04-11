package com.yaheen.o2park.bean;


public class TbChatMsg {

	private static final long serialVersionUID = 421209447912013905l;

	// Fields
	private String fromUserId;

	private String fromUserName;

	private String fromIp;

	private String toUserId;

	private String toUserName;

	private String msg;

	private String type;

	public Boolean getResult() {
		return result;
	}

	public void setResult(Boolean result) {
		this.result = result;
	}

	private Boolean result;
	// Constructors
	/** default constructor */
	public TbChatMsg() {
	}

	/** full constructor */
	public TbChatMsg(String fromUserId, String fromUserName, String fromIp, String toUserId, String toUserName, String msg, String type) {
		super();
		this.fromUserId = fromUserId;
		this.fromUserName = fromUserName;
		this.fromIp = fromIp;
		this.toUserId = toUserId;
		this.toUserName = toUserName;
		this.msg = msg;
		this.type = type;
	}

	// Property accessors
	public String getFromUserId() {
		return this.fromUserId;
	}

	public void setFromUserId(String fromUserId) {
		this.fromUserId = fromUserId;
	}

	public String getFromUserName() {
		return this.fromUserName;
	}

	public void setFromUserName(String fromUserName) {
		this.fromUserName = fromUserName;
	}

	public String getFromIp() {
		return this.fromIp;
	}

	public void setFromIp(String fromIp) {
		this.fromIp = fromIp;
	}

	public String getToUserId() {
		return this.toUserId;
	}

	public void setToUserId(String toUserId) {
		this.toUserId = toUserId;
	}

	public String getToUserName() {
		return this.toUserName;
	}

	public void setToUserName(String toUserName) {
		this.toUserName = toUserName;
	}

	public String getMsg() {
		return this.msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}




}
