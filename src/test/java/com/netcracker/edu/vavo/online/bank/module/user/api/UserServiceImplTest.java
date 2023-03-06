package com.netcracker.edu.vavo.online.bank.module.user.api;

import com.netcracker.edu.vavo.online.bank.exceptions.BusinessException;
import com.netcracker.edu.vavo.online.bank.module.account.api.AccountServiceImplTest;
import com.netcracker.edu.vavo.online.bank.module.user.api.UserServiceImpl;
import com.netcracker.edu.vavo.online.bank.module.user.model.UserCreationForm;
import com.netcracker.edu.vavo.online.bank.module.user.model.UserDTO;

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

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(initializers = {UserServiceImplTest.Initializer.class})
public class UserServiceImplTest {
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
    private UserServiceImpl service;

    @Test
    public void createNewUser_correct(){
        UserCreationForm form = new UserCreationForm();
        form.setName("test2name");
        form.setUsername("test12username");
        form.setPassword("qwertyQ21");

        UserDTO result = service.createNewUser(form);

        assertNotNull(result.getId());
        assertEquals(form.getUsername(), result.getUsername());
        assertEquals(form.getName(), result.getName());
    }

    @Test
    public void createNewUser_wrongCreationForm(){
        UserCreationForm form = new UserCreationForm();
        form.setName("tqdad");
        form.setUsername("te");
        form.setPassword("qwerty");

        BusinessException ex = assertThrows(BusinessException.class, () -> {
            UserDTO result = service.createNewUser(form);
        });

        assertEquals("ERR-002", ex.getCode());
    }

    @Test
    public void createNewUser_usernameDuplicate() {
        UserCreationForm form = new UserCreationForm();
        form.setName("test1name");
        form.setUsername("test1username");
        form.setPassword("qwerty12Q");

        service.createNewUser(form);

        BusinessException ex = assertThrows(BusinessException.class, () -> {
            UserDTO result = service.createNewUser(form);
        });

        assertEquals("ERR-001", ex.getCode());

    }

    @Test
    @WithMockUser("updateUser1")
    public void updateUser_correct() {
        UserCreationForm form = new UserCreationForm();
        form.setName("tqdad");
        form.setUsername("updateUser1");
        form.setPassword("qwerty213Q");

        UserDTO before = service.createNewUser(form);

        form.setUsername("newUpdateUser1");
        UserDTO update = service.updateUser(form);

        assertEquals(before.getId(), update.getId());
        assertEquals(before.getName(), update.getName());
        assertEquals("newUpdateUser1", update.getUsername());

    }

    @Test
    @WithMockUser("updateUser2")
    public void updateUser_wrongCreationForm() {
        UserCreationForm form = new UserCreationForm();
        form.setName("tqdad");
        form.setUsername("updateUser2");
        form.setPassword("qwerty213Q");

        UserDTO createdUser = service.createNewUser(form);

        BusinessException ex = assertThrows(BusinessException.class, () -> {
            service.updateUser(new UserCreationForm());
        });

        assertEquals("ERR-002", ex.getCode());
    }

    @Test
    @WithMockUser("updateUser3")
    public void updateUser_usernameDuplicate() {
        UserCreationForm form = new UserCreationForm();
        form.setName("tqdad");
        form.setUsername("updateUser3");
        form.setPassword("qwerty213Q");
        service.createNewUser(form);


        form.setName("tqdad");
        form.setUsername("updateUser31");
        form.setPassword("qwerty213Q");
        service.createNewUser(form);

        form.setUsername("updateUser31");
        BusinessException ex = assertThrows(BusinessException.class, () -> {
            service.updateUser(form);
        });

        assertEquals("ERR-001", ex.getCode());
    }

    @Test
    @WithMockUser("updateUser4")
    public void updateUser_sameUsername() {
        UserCreationForm form = new UserCreationForm();
        form.setName("tqdad");
        form.setUsername("updateUser4");
        form.setPassword("qwerty213Q");

        service.createNewUser(form);

        form.setName("adddda");

        UserDTO updated = service.updateUser(form);

        assertEquals(form.getName(), updated.getName());
        assertEquals(form.getUsername(), updated.getUsername());
    }


}