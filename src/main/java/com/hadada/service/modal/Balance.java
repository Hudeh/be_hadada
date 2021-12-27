package com.hadada.service.modal;

import lombok.Data;

import java.util.Date;

@Data
public class Balance {
    private String actual_balance;
    private String ledger_balance;
    private Date date;
}
