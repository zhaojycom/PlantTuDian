package com.zhaojy.planttudian.bean;

public class HistoryRequestParams {
	private String userPhone;
	private int browseId;
	private int browseSort;
	private String time;

	public String getUserPhone() {
		return userPhone;
	}

	public void setUserPhone(String userPhone) {
		this.userPhone = userPhone;
	}

	public int getBrowseId() {
		return browseId;
	}

	public void setBrowseId(int browseId) {
		this.browseId = browseId;
	}

	public int getBrowseSort() {
		return browseSort;
	}

	public void setBrowseSort(int browseSort) {
		this.browseSort = browseSort;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

}
