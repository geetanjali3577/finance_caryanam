package com.finserv.dto;



import lombok.Data;

@Data
public class RazorpayOrderResponse {

    private String orderId;
    private String currency;
    private Integer amount;
}
