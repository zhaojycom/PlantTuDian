package com.zhaojy.planttudian.bean;

/**
 * @author: zhaojy
 * @data:On 2018/9/25.
 */

public class RequestParams {
    private int offset;
    private int limit;

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
