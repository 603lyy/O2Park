package com.yaheen.o2park.bean;

/**
 * 一个消息的JavaBean
 * 
 * @author way
 * 
 */
public class ChatMsgEntity {
	private String name;//消息来自
	private String date;//消息日期
	private String message;//消息内容


	public void setType(Integer type) {
		this.type = type;
	}

	public Integer getType() {
		return type;
	}

	private Integer type;  //消息类型，0.文字，1.图片
	private boolean isComMeg = true;// 是否为收到的消息

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public boolean getMsgType() {
		return isComMeg;
	}

	public void setMsgType(boolean isComMsg) {
		isComMeg = isComMsg;
	}

	public ChatMsgEntity() {
	}

	public ChatMsgEntity(String name, String date, String text, boolean isComMsg, Integer type) {
		super();
		this.name = name;
		this.date = date;
		this.message = text;
		this.isComMeg = isComMsg;
		this.type=type;
	}

}
