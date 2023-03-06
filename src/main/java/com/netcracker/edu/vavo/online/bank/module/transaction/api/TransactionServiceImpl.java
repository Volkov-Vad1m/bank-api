package com.netcracker.edu.vavo.online.bank.module.transaction.api;

import com.netcracker.edu.vavo.online.bank.exceptions.BusinessExceptionManager;
import com.netcracker.edu.vavo.online.bank.module.account.AccountService;
import com.netcracker.edu.vavo.online.bank.module.account.model.AccountDTO;
import com.netcracker.edu.vavo.online.bank.module.account.model.AccountUpdateForm;
import com.netcracker.edu.vavo.online.bank.module.transaction.TransactionService;
import com.netcracker.edu.vavo.online.bank.module.transaction.dao.TransactionEntity;
import com.netcracker.edu.vavo.online.bank.module.transaction.dao.TransactionRepository;
import com.netcracker.edu.vavo.online.bank.module.transaction.model.ExternalTransactionForm;
import com.netcracker.edu.vavo.online.bank.module.transaction.model.TransactionDTO;
import com.netcracker.edu.vavo.online.bank.module.transaction.model.InternalTransactionForm;
import com.netcracker.edu.vavo.online.bank.module.transaction.model.TransactionType;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
public class TransactionServiceImpl implements TransactionService {


    private final TransactionRepository repository;
    private final AccountService accountService;
    private final BusinessExceptionManager exceptionManager;


    public TransactionServiceImpl(TransactionRepository repository,
                                  AccountService accountService,
                                  BusinessExceptionManager exceptionManager) {
        this.repository = repository;
        this.accountService = accountService;
        this.exceptionManager = exceptionManager;
    }


    public List<TransactionDTO> findAllTransactionsByNumber(Long accountNumber) {
        List<TransactionEntity> transactions = repository.findAllByAccountNumberOrderByTime(accountNumber);
        return transactions.stream().map(TransactionDTO::fromEntity).toList();
    }

    public List<TransactionDTO> getAllCurrentUserTransactions() {
        return findAllTransactionsByNumber(accountService.getCurrentAccountInfo().getNumber());
    }


    public TransactionDTO externalTransfer(ExternalTransactionForm form) {
        validateNegativeValue(form.getValue());

        AccountDTO account = accountService.getCurrentAccountInfo();

        TransactionEntity transaction = TransactionEntity.builder()
                .accountNumber(account.getNumber())
                .type(form.getType())
                .value(form.getValue())
                .time(LocalDateTime.now())
                .build();


        if(form.getType() == TransactionType.INCOMING_TRANSFER) {
            accountService.updateBalance(
                    AccountUpdateForm.builder()
                            .number(account.getNumber())
                            .newBalance(account.getBalance() + form.getValue())
                            .build());

        } else if(form.getType() == TransactionType.OUTGOING_TRANSFER) {

            validateEnoughMoneyOnAccount(account, form.getValue());

            accountService.updateBalance(
                    AccountUpdateForm.builder()
                            .number(account.getNumber())
                            .newBalance(account.getBalance() - form.getValue())
                            .build());

        }

        return TransactionDTO.fromEntity(repository.save(transaction));
    }


    @Override
    public List<TransactionDTO> internalTransfer(InternalTransactionForm form) {
        validateNegativeValue(form.getValue());

        AccountDTO sender = accountService.getCurrentAccountInfo();
        AccountDTO receiver = accountService.findAccountByNumber(form.getReceiverNumber());

        validateEnoughMoneyOnAccount(sender, form.getValue());

        accountService.updateBalance(
                AccountUpdateForm.builder()
                        .number(sender.getNumber())
                        .newBalance(sender.getBalance() - form.getValue())
                        .build());

        accountService.updateBalance(
                AccountUpdateForm.builder()
                        .number(receiver.getNumber())
                        .newBalance(receiver.getBalance() + form.getValue())
                        .build());

        TransactionEntity ofSender = TransactionEntity.builder()
                .accountNumber(sender.getNumber())
                .type(TransactionType.OUTGOING_TRANSFER)
                .value(form.getValue())
                .time(LocalDateTime.now())
                .build();

        TransactionEntity ofReceiver = TransactionEntity.builder()
                .accountNumber(receiver.getNumber())
                .type(TransactionType.INCOMING_TRANSFER)
                .value(form.getValue())
                .time(LocalDateTime.now())
                .build();

        return repository.saveAll(List.of(ofSender, ofReceiver)).stream()
                .map(TransactionDTO::fromEntity).collect(Collectors.toList());

    }


    //__________________________________________
    //validation
    //__________________________________________
    private void validateNegativeValue(long value) {
        if(value < 0) {
            exceptionManager.throwsException(
                    "ERR-007",
                    Map.of("value", value)
            );
        }
    }

    private void validateEnoughMoneyOnAccount(AccountDTO account, long value) {
        if(account.getBalance() < value) {
            exceptionManager.throwsException(
                    "ERR-005",
                    Map.of("required_value", value,
                            "available_value", account.getBalance()
                    )
            );
        }
    }

}