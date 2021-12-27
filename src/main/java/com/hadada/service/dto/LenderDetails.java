package com.hadada.service.dto;

import lombok.Data;

@Data
public class LenderDetails {
    private Long customerId ;
    private String logoUrl;
    private String brandName;
    private String callBackUrl;
}
