package com.st.cms.entity;

/**
 * Created by jt on 2016/10/10.
 */
public class Injury {

    //部位
    private String position;
    //描述
    private String depict;

    public Injury() {
    }


    public Injury(String position, String depict) {
        this.position = position;
        this.depict = depict;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getDepict() {
        return depict;
    }

    public void setDepict(String depict) {
        this.depict = depict;
    }
}
