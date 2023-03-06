package com.netcracker.edu.vavo.online.bank.module.account.api;

import com.netcracker.edu.vavo.online.bank.exceptions.BusinessExceptionManager;
import com.netcracker.edu.vavo.online.bank.module.account.AccountService;
import com.netcracker.edu.vavo.online.bank.module.account.dao.AccountEntity;
import com.netcracker.edu.vavo.online.bank.module.account.dao.AccountRepository;
import com.netcracker.edu.vavo.online.bank.module.account.model.AccountDTO;
import com.netcracker.edu.vavo.online.bank.module.account.model.AccountUpdateForm;
import com.netcracker.edu.vavo.online.bank.module.user.api.UserHelper;
import com.netcracker.edu.vavo.online.bank.module.user.dao.UserEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository repository;
    private final BusinessExceptionManager exceptionManager;
    private final UserHelper userHelper;

    public AccountServiceImpl(AccountRepository repository,
                              BusinessExceptionManager exceptionManager, UserHelper userHelper) {
        this.repository = repository;
        this.exceptionManager = exceptionManager;
        this.userHelper = userHelper;
    }

    @Override
    public List<AccountDTO> findAllAccounts() {
        return repository.findAll().stream().map(AccountDTO::fromEntity).toList();
    }

    /**
     * Сервисный метод. Не будет в REST. Вызываем его в Transaction
     */
    public AccountDTO findAccountByNumber(Long number) {
        Optional<AccountEntity> accountOpt = repository.findByNumber(number);

        validateAccountNotFound(accountOpt, number);

        return AccountDTO.fromEntity(accountOpt.get());
    }

    @Override
    public AccountDTO getCurrentAccountInfo() {

        Optional<AccountEntity> accountOpt = repository.findByUser(userHelper.getCurrentUserEntity());
        validateAccountNotCreatedYet(accountOpt);

        return AccountDTO.fromEntity(accountOpt.get());
    }

    @Override
    public AccountDTO createAccount() {
        UserEntity currentUserEntity = userHelper.getCurrentUserEntity();

        validateAccountAlreadyExists(currentUserEntity);

        AccountEntity newAccount = AccountEntity.builder()
                .id(UUID.randomUUID())
                .number(generateUniqueLong())
                .balance(0L)
                .user(currentUserEntity).build();

        return AccountDTO.fromEntity(repository.save(newAccount));
    }



    /**
     * метод чисто сервесный не будет вызываться в REST
     * делаем валидацию здесь
     */
    @Override
    public AccountDTO updateBalance(AccountUpdateForm form) {

        Optional<AccountEntity> accountOpt = repository.findByNumber(form.getNumber());
        validateAccountNotFound(accountOpt, form.getNumber());

        AccountEntity currentAccount = accountOpt.get();
        currentAccount.setBalance(form.getNewBalance());

        return AccountDTO.fromEntity(repository.save(currentAccount));


    }
    private Long generateUniqueLong() {
        return UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE;
    }

    //__________________________________________
    //validation
    //__________________________________________
    private void validateAccountAlreadyExists(UserEntity currentUserEntity) {
        Optional<AccountEntity> currentAccount = repository.findByUser(currentUserEntity);
        currentAccount.ifPresent(accountEntity -> exceptionManager.throwsException(
                "ERR-003",
                Map.of("account", AccountDTO.fromEntity(accountEntity))
        ));
    }

    private void validateAccountNotFound(Optional<AccountEntity> accountOpt, Long number) {
        if (accountOpt.isEmpty()) {
            exceptionManager.throwsException("ERR-006",
                    Map.of("number", number)
            );
        }
    }

    private void validateAccountNotCreatedYet(Optional<AccountEntity> accountOpt) {
        if (accountOpt.isEmpty()) {
            exceptionManager.throwsException(
                    "ERR-004", null);
        }
    }
}
