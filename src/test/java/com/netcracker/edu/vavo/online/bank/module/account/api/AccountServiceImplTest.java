package com.netcracker.edu.vavo.online.bank.module.account.api;

import com.netcracker.edu.vavo.online.bank.exceptions.BusinessException;
import com.netcracker.edu.vavo.online.bank.module.account.dao.AccountRepository;
import com.netcracker.edu.vavo.online.bank.module.account.model.AccountDTO;
import com.netcracker.edu.vavo.online.bank.module.account.model.AccountUpdateForm;
import com.netcracker.edu.vavo.online.bank.module.user.dao.UserEntity;
import com.netcracker.edu.vavo.online.bank.module.user.dao.UserRepository;
import org.junit.*;
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

import java.util.UUID;

import static org.junit.Assert.*;


@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(initializers = {AccountServiceImplTest.Initializer.class})
public class AccountServiceImplTest {
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
    private AccountServiceImpl service;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private UserRepository userRepository;

    @Before
    public  void setup() {
        UserEntity save = userRepository.save(UserEntity.builder()
                .id(UUID.randomUUID())
                .name("test name")
                .username("testUser")
                .passwordHash("HashMash12")
                .build());

    }

    @After
    public void delete() {
        accountRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @WithMockUser(username = "testUser")
    public void createAccount_correct() {

        AccountDTO result = service.createAccount();

        assertNotNull(result.getNumber());
        assertEquals(result.getBalance().longValue(), 0L);

    }

    @Test
    @WithMockUser(username = "testUser")
    public void createAccount_alreadyExists() {

        service.createAccount();

        BusinessException ex = assertThrows(BusinessException.class, () -> {
            service.createAccount();
        });
        assertEquals("ERR-003", ex.getCode());

    }

    @Test
    @WithMockUser(username = "testUser")
    public void getCurrentAccountInfo_correct() {

        service.createAccount();

        AccountDTO result = service.getCurrentAccountInfo();

        assertNotNull(result.getNumber());
        assertEquals(result.getBalance().longValue(), 0L);

    }

    @Test
    @WithMockUser(username = "testUser")
    public void getCurrentAccountInfo_notExists() {

        BusinessException ex = assertThrows(BusinessException.class, () -> {
            service.getCurrentAccountInfo();
        });

        assertEquals("ERR-004", ex.getCode());
    }

    @Test
    public void findAccountByNumber_notFound() {
        BusinessException ex = assertThrows(BusinessException.class, () -> {
            service.findAccountByNumber(666L);
        });
        assertEquals("ERR-006", ex.getCode());
    }

    @Test
    @WithMockUser(username = "testUser")
    public void updateBalance_correct() {


        AccountDTO account = service.createAccount();

        AccountDTO result = service.updateBalance(AccountUpdateForm.builder()
                .number(account.getNumber())
                .newBalance(100500L)
                .build());

        assertEquals(account.getNumber(), result.getNumber());
        assertEquals(100500L, result.getBalance().longValue());
    }


    @Test
    public void updateBalance_accountNotFound() {

        BusinessException ex = assertThrows(BusinessException.class, () -> {
            service.updateBalance(AccountUpdateForm.builder()
                    .number(666L)
                    .newBalance(100500L)
                    .build());
        });

        assertEquals("ERR-006", ex.getCode());

    }


}