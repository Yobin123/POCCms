package com.st.cms.entity;

/**
 * Created by jt on 2016/10/10.
 */
public class Treatment {
    //名称
    private String name;
    //使用量
    private int consume;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getConsume() {
        return consume;
    }

    public void setConsume(int consume) {
        this.consume = consume;
    }

    public Treatment(String name, int consume) {
        this.name = name;
        this.consume = consume;
    }
}
