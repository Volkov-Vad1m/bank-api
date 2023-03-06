package com.netcracker.edu.vavo.online.bank.module.transaction.model;

import com.netcracker.edu.vavo.online.bank.module.transaction.dao.TransactionEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDTO {
    private UUID id;
    private Long accountNumber;
    private TransactionType type;
    private Integer value;
    private LocalDateTime time;

    public static TransactionDTO fromEntity(TransactionEntity entity) {
            TransactionDTO result = new TransactionDTO();
            result.setId(entity.getId());
            result.setAccountNumber(entity.getAccountNumber());
            result.setType(entity.getType());
            result.setValue(entity.getValue());
            result.setTime(entity.getTime());
            return result;
    }
}
