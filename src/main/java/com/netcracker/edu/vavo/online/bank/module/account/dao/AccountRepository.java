package com.netcracker.edu.vavo.online.bank.module.account.dao;

import com.netcracker.edu.vavo.online.bank.module.user.dao.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccountRepository extends JpaRepository<AccountEntity, UUID> {

    Optional<AccountEntity> findByUser(UserEntity user);


    Optional<AccountEntity> findByNumber(Long number);
}
