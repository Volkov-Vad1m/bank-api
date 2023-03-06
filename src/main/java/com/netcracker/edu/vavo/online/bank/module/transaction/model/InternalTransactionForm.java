package com.netcracker.edu.vavo.online.bank.module.transaction.model;

import lombok.Data;

@Data
public class InternalTransactionForm {

    private Long receiverNumber;
    private Integer value;

}