package com.netcracker.edu.vavo.online.bank.module.account;

import com.netcracker.edu.vavo.online.bank.module.account.model.AccountDTO;
import com.netcracker.edu.vavo.online.bank.module.account.model.AccountUpdateForm;

import java.util.List;

public interface AccountService {
    List<AccountDTO> findAllAccounts();
    AccountDTO getCurrentAccountInfo();
    AccountDTO createAccount();

    AccountDTO updateBalance(AccountUpdateForm form);

    AccountDTO findAccountByNumber(Long number);

}