package com.st.cms.entity;

/**
 * Created by jt on 2016/8/31.
 */
public class MedicalRes {
    private String id;
    private String name;
    private int qty;
//    private String hospitalId;
    //0代表血型，1代表医疗物品，2代表药品
    private int type;
//    private int used;
    private long timestamp;
    private int consume;
    public MedicalRes() {
    }

    public MedicalRes(String name, int qty, int consume, int type) {
        this.name = name;
        this.qty = qty;
        this.type = type;
        this.consume = consume;
    }

//    public MedicalRes(String id, String name, int qty, int type) {
//        this.id = id;
//        this.name = name;
//        this.qty = qty;
//        this.type = type;
//    }

    public MedicalRes(String id, String name, int qty, int type, int consume) {
        this.id = id;
        this.name = name;
        this.qty = qty;
        this.type = type;
        this.consume = consume;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getConsume() {
        return consume;
    }

    public void setConsume(int consume) {
        this.consume = consume;
    }

    @Override
    public String toString() {
        return "MedicalRes{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", qty=" + qty +
                ", type=" + type +
                ", timestamp=" + timestamp +
                ", consume=" + consume +
                '}';
    }
}
