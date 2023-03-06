package com.netcracker.edu.vavo.online.bank.module.account.model;

import com.netcracker.edu.vavo.online.bank.module.account.dao.AccountEntity;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class AccountDTO {
    private UUID id;
    private Long number;
    private Long balance;

    public static AccountDTO fromEntity(AccountEntity entity) {
        return AccountDTO.builder()
                .id(entity.getId())
                .number(entity.getNumber())
                .balance(entity.getBalance())
                .build();
    }
}
