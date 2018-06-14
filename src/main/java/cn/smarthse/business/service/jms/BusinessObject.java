package cn.smarthse.business.service.jms;

import java.io.Serializable;

public class BusinessObject implements Serializable{
	
	private static final long serialVersionUID = 1602978059663401852L;
	
	private int index;
	private String detail;

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

}
