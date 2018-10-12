package com.zhaojy.planttudian.bean;

/**
 * @author: zhaojy
 * @data:On 2018/9/20.
 */

public class ArticleItemBean {
    private String articleType;
    private String imgUrl;
    private String title;
    private String briefContent;

    public String getArticleType() {
        return articleType == null ? "" : articleType;
    }

    public void setArticleType(String articleType) {
        this.articleType = articleType;
    }

    public String getBriefContent() {
        return briefContent == null ? "" : briefContent;
    }

    public void setBriefContent(String briefContent) {
        this.briefContent = briefContent;
    }

    public String getImgUrl() {
        return imgUrl == null ? "" : imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getTitle() {
        return title == null ? "" : title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
