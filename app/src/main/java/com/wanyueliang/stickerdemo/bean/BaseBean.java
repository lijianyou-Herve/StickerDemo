package com.wanyueliang.stickerdemo.bean;

import com.wanyueliang.stickerdemo.utils.JsonUtils;

import java.io.Serializable;


public abstract class BaseBean<T> implements Serializable, IDB<T> {


    /**
     *
     */
    private static final long serialVersionUID = 5990672132624172041L;

    /**
     * 将json对象转化为Bean实例
     *
     * @param json
     * @return
     */
    public T parseJSON(String json) {
        return (T) JsonUtils.fromJson(json, this.getClass());
    }

    /**
     * 将Bean实例转化为json对象
     *
     * @return
     */
    public String toJson() {
        return JsonUtils.toJson(this);
    }


}
