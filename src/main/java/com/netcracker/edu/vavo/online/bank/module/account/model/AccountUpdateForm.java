package com.netcracker.edu.vavo.online.bank.module.account.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AccountUpdateForm {
    private Long number;
    private Long newBalance;
}
