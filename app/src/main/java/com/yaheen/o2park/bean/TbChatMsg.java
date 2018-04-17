package com.yaheen.o2park.bean;


public class TbChatMsg {

	private static final long serialVersionUID = 421209447912013905l;

	public TbChatMsg(String userName, String sex, String company) {
		this.userName = userName;
		this.sex = sex;
		this.unit  = unit ;
	}

	private String userName;

	private String sex;

	private String unit ;

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}
}
