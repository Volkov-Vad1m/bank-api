package com.netcracker.edu.vavo.online.bank.module.transaction.api;

import com.netcracker.edu.vavo.online.bank.exceptions.BusinessException;
import com.netcracker.edu.vavo.online.bank.module.account.dao.AccountEntity;
import com.netcracker.edu.vavo.online.bank.module.account.dao.AccountRepository;
import com.netcracker.edu.vavo.online.bank.module.transaction.model.ExternalTransactionForm;
import com.netcracker.edu.vavo.online.bank.module.transaction.model.TransactionDTO;
import com.netcracker.edu.vavo.online.bank.module.transaction.model.InternalTransactionForm;
import com.netcracker.edu.vavo.online.bank.module.transaction.model.TransactionType;
import com.netcracker.edu.vavo.online.bank.module.user.dao.UserEntity;
import com.netcracker.edu.vavo.online.bank.module.user.dao.UserRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(initializers = {TransactionServiceImplTest.Initializer.class})
public class TransactionServiceImplTest {

    @ClassRule
    @SuppressWarnings("all")
    public static PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer("postgres:11.1")
            .withDatabaseName("integration-tests-db")
            .withUsername("sa")
            .withPassword("sa");


    static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues.of(
                    "spring.datasource.url=" + postgreSQLContainer.getJdbcUrl(),
                    "spring.datasource.username=" + postgreSQLContainer.getUsername(),
                    "spring.datasource.password=" + postgreSQLContainer.getPassword()
            ).applyTo(configurableApplicationContext.getEnvironment());
        }
    }

    @Autowired
    private TransactionServiceImpl service;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AccountRepository accountRepository;

    @Before
    public  void setup() {
        UserEntity sender = userRepository.save(UserEntity.builder()
                .id(UUID.randomUUID())
                .name("test name")
                .username("testUserSender")
                .passwordHash("HashMash12")
                .build());
        UserEntity receiver = userRepository.save(UserEntity.builder()
                .id(UUID.randomUUID())
                .name("test name")
                .username("testUserReceiver")
                .passwordHash("HashMash12")
                .build());
        accountRepository.save(AccountEntity.builder()
                .number(1234L)
                .balance(1000L)
                .user(sender).build());
        accountRepository.save(AccountEntity.builder()
                .number(4321L)
                .balance(1000L)
                .user(receiver).build());

    }

    @After
    public void delete() {
        accountRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @WithMockUser("testUserSender")
    public void internalTransfer_correct() {
        InternalTransactionForm form = new InternalTransactionForm();
        form.setReceiverNumber(4321L);
        form.setValue(400);

        List<TransactionDTO> result = service.internalTransfer(form);
        TransactionDTO ofSender = result.get(0);
        TransactionDTO ofReceiver = result.get(1);

        assertEquals(600L ,accountRepository.findByNumber(1234L).get().getBalance());
        assertEquals(1400L ,accountRepository.findByNumber(4321L).get().getBalance());

        assertEquals(400, ofSender.getValue());
        assertEquals(TransactionType.OUTGOING_TRANSFER, ofSender.getType());
        assertEquals(1234L, ofSender.getAccountNumber());

        assertEquals(400, ofReceiver.getValue());
        assertEquals(TransactionType.INCOMING_TRANSFER, ofReceiver.getType());
        assertEquals(4321L, ofReceiver.getAccountNumber());


    }

    @Test
    @WithMockUser("testUserSender")
    public void internalTransfer_negativeValue() {
        InternalTransactionForm form = new InternalTransactionForm();
        form.setReceiverNumber(4321L);
        form.setValue(-400);

        BusinessException ex = assertThrows(BusinessException.class, () -> {
            service.internalTransfer(form);
        });

        assertEquals("ERR-007", ex.getCode());

    }

    @Test
    @WithMockUser("testUserSender")
    public void internalTransfer_notEnoughMoney() {
        InternalTransactionForm form = new InternalTransactionForm();
        form.setReceiverNumber(4321L);
        form.setValue(100500);

        BusinessException ex = assertThrows(BusinessException.class, () -> {
            service.internalTransfer(form);
        });

        assertEquals("ERR-005", ex.getCode());

    }

    @Test
    @WithMockUser("testUserSender")
    public void internalTransfer_ReceiverNotFound() {
        InternalTransactionForm form = new InternalTransactionForm();
        form.setReceiverNumber(100500L);
        form.setValue(400);

        BusinessException ex = assertThrows(BusinessException.class, () -> service.internalTransfer(form) );

        assertEquals("ERR-006", ex.getCode());

    }

    @Test
    @WithMockUser("testUserSender")
    public void externalTransfer_correctIncoming() {
        ExternalTransactionForm form = new ExternalTransactionForm();
        form.setType(TransactionType.INCOMING_TRANSFER);
        form.setValue(100);

        TransactionDTO transaction = service.externalTransfer(form);

        assertEquals(100, transaction.getValue());
        assertEquals(TransactionType.INCOMING_TRANSFER, transaction.getType());
        assertEquals(1234L, transaction.getAccountNumber());

        AccountEntity account = accountRepository.findByNumber(1234L).get();

        assertEquals(1100, account.getBalance());
    }

    @Test
    @WithMockUser("testUserSender")
    public void externalTransfer_correctOutgoing() {
        ExternalTransactionForm form = new ExternalTransactionForm();
        form.setType(TransactionType.OUTGOING_TRANSFER);
        form.setValue(100);

        TransactionDTO transaction = service.externalTransfer(form);

        assertEquals(100, transaction.getValue());
        assertEquals(TransactionType.OUTGOING_TRANSFER, transaction.getType());
        assertEquals(1234L, transaction.getAccountNumber());

        AccountEntity account = accountRepository.findByNumber(1234L).get();

        assertEquals(900, account.getBalance());
    }



    @Test
    @WithMockUser("testUserSender")
    public void externalTransfer_notEnoughMoney() {
        ExternalTransactionForm form = new ExternalTransactionForm();
        form.setType(TransactionType.OUTGOING_TRANSFER);
        form.setValue(1001);


        BusinessException ex = assertThrows(BusinessException.class, () -> service.externalTransfer(form));
        assertEquals("ERR-005", ex.getCode());

    }

    @Test
    @WithMockUser("testUserSender")
    public void externalTransfer_negativeValue() {
        ExternalTransactionForm form = new ExternalTransactionForm();
        form.setType(TransactionType.OUTGOING_TRANSFER);
        form.setValue(-1001);


        BusinessException ex = assertThrows(BusinessException.class, () -> service.externalTransfer(form));
        assertEquals("ERR-007", ex.getCode());

    }





}