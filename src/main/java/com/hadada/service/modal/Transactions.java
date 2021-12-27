package com.hadada.service.modal;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Transactions {
    private String account_number;
    private String account_type;
    private String currency;
    private String account_name;
    private String account_status;
    List <Transaction> transaction;

}
