package com.dispatcher.dispatcher.entity;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@XmlRootElement
public class Order implements Serializable {

    private Integer target_id;
    private Integer dispatched_id;
    private  String data;
    private Integer client_id;

    public Order() {
    }

    public Order(Integer target_id, Integer dispatched_id, String data, Integer clinet_id) {
        this.target_id = target_id;
        this.dispatched_id = dispatched_id;
        this.data = data;
        this.client_id = clinet_id;
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

    public Integer getClient_id() {
        return client_id;
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
    public void setClient_id(Integer client_id) {
        this.client_id = client_id;
    }
}
