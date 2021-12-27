package com.hadada.service.modal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Transaction {
    String transcation_date;
    String transcation_type;
    String transcation_amount;
    String debitOrCredit;
    String transcation_description;

}
