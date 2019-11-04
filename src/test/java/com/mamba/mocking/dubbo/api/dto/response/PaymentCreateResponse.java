package com.mamba.mocking.dubbo.api.dto.response;

import lombok.ToString;

import java.io.Serializable;

@ToString
public class PaymentCreateResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long payId;

    private String orderId;

    private String outPayId;

    private String outOrderId;

    public Long getPayId() {
        return payId;
    }

    public void setPayId(Long payId) {
        this.payId = payId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getOutPayId() {
        return outPayId;
    }

    public void setOutPayId(String outPayId) {
        this.outPayId = outPayId;
    }

    public String getOutOrderId() {
        return outOrderId;
    }

    public void setOutOrderId(String outOrderId) {
        this.outOrderId = outOrderId;
    }
}
