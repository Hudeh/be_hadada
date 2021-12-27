package com.hadada.service.modal;

import lombok.Data;

import java.util.List;

@Data
public class User {
    private String bvn;
    private String account_ID;
    private String bank_code;
    private List<Account> accounts;
}
