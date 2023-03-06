package com.netcracker.edu.vavo.online.bank.module.account.dao;


import com.netcracker.edu.vavo.online.bank.module.user.dao.UserEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@NoArgsConstructor
@Data
@Table(name = "accounts")
@Builder
@AllArgsConstructor
public class AccountEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "number", nullable = false, unique = true)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long number;


    @Column(name = "balance")
    private Long balance;

    @OneToOne
    private UserEntity user;

}
