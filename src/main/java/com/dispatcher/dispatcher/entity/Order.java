package com.dispatcher.dispatcher.entity;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@XmlRootElement
public class Order implements Serializable {

    private Integer target_id;
    private Integer dispatched_id;
    private  String data;
    private Integer clinet_id;

    public Order() {
    }

    public Order(Integer target_id, Integer dispatched_id, String data, Integer clinet_id) {
        this.target_id = target_id;
        this.dispatched_id = dispatched_id;
        this.data = data;
        this.clinet_id = clinet_id;
    }

    public Integer getTarget_id() {
        return target_id;
    }

    public Integer getDispatched_id() {
        return dispatched_id;
    }

    public String getData() {
        return data;
    }

    public Integer getClinet_id() {
        return clinet_id;
    }


    @XmlElement
    public void setDispatched_id(Integer dispatched_id) {
        this.dispatched_id = dispatched_id;
    }

    @XmlElement
    public void setData(String data) {
        this.data = data;
    }

    @XmlElement
    public void setTarget_id(Integer target_id) {
        this.target_id = target_id;
    }

    @XmlElement
    public void setClinet_id(Integer clinet_id) {
        this.clinet_id = clinet_id;
    }
}
