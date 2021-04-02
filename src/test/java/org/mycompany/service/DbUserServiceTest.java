package org.mycompany.service;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.rules.ExpectedException;
import org.mycompany.entity.User;
import org.mycompany.repository.UserRepository;
import org.mycompany.service.exception.UserServiceException;
import org.mycompany.service.user.DbUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@TestPropertySource(properties = {
        "spring.main.banner-mode=off",
        "spring.datasource.platform=h2",
        "spring.jpa.hibernate.ddl-auto=none"
})
public class DbUserServiceTest {
    private static final String USER1 = "user1";

    @Autowired
    UserRepository userRepository;
    private DbUserService dbUserService;

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @BeforeEach
    public void setUp() {
        dbUserService = new DbUserService(userRepository);
    }

    @Test
    void addMoneyForUnknownUser() throws UserServiceException {
        Assert.assertEquals("Добавление денег выполнено успешно", dbUserService.addMoney(USER1, 100));
    }

    @Test
    void addMoneyForKnownUser() throws UserServiceException {
        dbUserService.getUser(USER1);
        Assert.assertEquals("Добавление денег выполнено успешно", dbUserService.addMoney(USER1, 100));
    }

    @Test
    void addMoneyForInvalidCountMoney() {
        Exception exception = assertThrows(UserServiceException.class, () -> {
            dbUserService.addMoney(USER1, -100);
        });
        Assert.assertTrue(exception.getMessage().equals("Сумма должна быть больше 0"));
    }

    @Test
    void getMoneyForKnownUser() throws UserServiceException {
        User user = dbUserService.getUser(USER1);
        user.setMoney(100);
        userRepository.save(user);
        Assert.assertEquals("Снятие денег выполнено успешно", dbUserService.getMoney(USER1, 100));
    }

    @Test
    void getMoneyForUnknownUser() {
        Exception exception = assertThrows(UserServiceException.class, () -> {
            dbUserService.getMoney(USER1, 100);
        });
        Assert.assertTrue(exception.getMessage().equals("Пользователя с именем " + USER1 + " не существует"));
    }

    @Test
    void getMoneyForInvalidCountMoney() {
        Exception exception = assertThrows(UserServiceException.class, () -> {
            dbUserService.getMoney(USER1, -100);
        });
        Assert.assertTrue(exception.getMessage().equals("Сумма должна быть больше 0"));
    }

    @Test
    void getMoneyInsufficientFunds() {
        dbUserService.getUser(USER1);
        Exception exception = assertThrows(UserServiceException.class, () -> {
            dbUserService.getMoney(USER1, 1);
        });
        Assert.assertTrue(exception.getMessage().equals("Запрашиваемая сумма больше, чем доступная для пользователя " + USER1));
    }
}