package com.zhaojy.planttudian.bean;

public class CollectionRequestParams {
	private String userPhone;
	private int collectId;
	private int collectSort;
	private String time;

	public String getUserPhone() {
		return userPhone;
	}

	public void setUserPhone(String userPhone) {
		this.userPhone = userPhone;
	}

	public int getCollectId() {
		return collectId;
	}

	public void setCollectId(int collectId) {
		this.collectId = collectId;
	}

	public int getCollectSort() {
		return collectSort;
	}

	public void setCollectSort(int collectSort) {
		this.collectSort = collectSort;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

}
