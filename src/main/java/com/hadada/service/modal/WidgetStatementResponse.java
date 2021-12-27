package com.hadada.service.modal;

import lombok.Data;

@Data
public class WidgetStatementResponse {
    String status;
    Boolean success;
    String message;
    String accountId;
    String bankName;
    String callBackUrl;
}
