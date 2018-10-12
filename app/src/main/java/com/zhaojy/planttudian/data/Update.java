package com.zhaojy.planttudian.data;

/**
 * @author: zhaojy
 * @data:On 2018/2/26.
 */

public interface Update extends BaseUpdate{
    void onSuccess(Object object);

    void onError(String result);
}
