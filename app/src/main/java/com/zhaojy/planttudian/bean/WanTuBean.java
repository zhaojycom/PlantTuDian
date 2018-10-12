package com.zhaojy.planttudian.bean;

/**
 * @author: zhaojy
 * @data:On 2018/9/25.
 */

public class WanTuBean {
    private int id;
    private String sImgUrl;
    private String bImgUrl;
    private String title;
    private String content;

    public String getbImgUrl() {
        return bImgUrl == null ? "" : bImgUrl;
    }

    public void setbImgUrl(String bImgUrl) {
        this.bImgUrl = bImgUrl;
    }

    public String getContent() {
        return content == null ? "" : content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getsImgUrl() {
        return sImgUrl == null ? "" : sImgUrl;
    }

    public void setsImgUrl(String sImgUrl) {
        this.sImgUrl = sImgUrl;
    }

    public String getTitle() {
        return title == null ? "" : title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}
