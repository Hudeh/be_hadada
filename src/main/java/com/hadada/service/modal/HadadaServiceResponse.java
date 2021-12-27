package com.hadada.service.modal;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class HadadaServiceResponse {
    String status;
    String walletStatus;
    Boolean hadadaAuthentication = false;
    Boolean bankAuthentication = false;
    Boolean validAppKey = false;
    String callBackUrl = "";
    String data = "";
    String accountId;
    String bankName;
    String email;
}
