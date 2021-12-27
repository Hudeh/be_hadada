package com.hadada.service.modal;

import lombok.Data;

@Data
public class Account {
    private String account_number;
    private String account_type;
    private String currency;
    private String account_name;
    private String account_status;
    private Balance balance;
}
