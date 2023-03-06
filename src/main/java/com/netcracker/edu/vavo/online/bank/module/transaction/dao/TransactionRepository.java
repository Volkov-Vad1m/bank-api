package com.netcracker.edu.vavo.online.bank.module.transaction.dao;

import com.netcracker.edu.vavo.online.bank.module.account.dao.AccountEntity;
import com.netcracker.edu.vavo.online.bank.module.account.model.AccountDTO;
import com.netcracker.edu.vavo.online.bank.module.transaction.model.TransactionDTO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<TransactionEntity, UUID> {
    List<TransactionEntity> findAllByAccountNumberOrderByTime(Long accountNumber);

}
