package com.zhaojy.planttudian.bean;

/**
 * @author: zhaojy
 * @data:On 2018/9/27.
 */

public class ClassifyRequestParams {
    private int classifyId;
    private int offset;
    private int limit;

    public int getClassifyId() {
        return classifyId;
    }

    public void setClassifyId(int classifyId) {
        this.classifyId = classifyId;
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
