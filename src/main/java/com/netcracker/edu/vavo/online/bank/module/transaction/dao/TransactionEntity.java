package com.netcracker.edu.vavo.online.bank.module.transaction.dao;


import com.netcracker.edu.vavo.online.bank.module.transaction.model.TransactionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Builder
@AllArgsConstructor
@Table(name = "transactions")
public class TransactionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    // связан с текущим пользователем
    @Column(name = "number")
    private Long accountNumber;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private TransactionType type;

    @Column(name = "time")
    private LocalDateTime time;

    @Column(name = "value")
    private Integer value;
}
