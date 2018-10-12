package com.zhaojy.planttudian.bean;

/**
 * @author: zhaojy
 * @data:On 2018/10/9.
 */

public class KeywordRequestParams {
    private String keyword;
    private int offset;
    private int limit;

    public String getKeyword() {
        return keyword == null ? "" : keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }
}
