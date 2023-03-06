package com.netcracker.edu.vavo.online.bank.module.transaction.model;

import lombok.Data;

@Data
public class ExternalTransactionForm {
    private TransactionType type;

    private Integer value;

}
