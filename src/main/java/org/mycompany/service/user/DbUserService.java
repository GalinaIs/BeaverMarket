package org.mycompany.service.user;

import org.mycompany.entity.User;
import org.mycompany.repository.UserRepository;
import org.mycompany.service.exception.UserServiceException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(isolation = Isolation.REPEATABLE_READ)
public class DbUserService implements UserService {
    private final UserRepository userRepository;

    public DbUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User getUser(String userName) {
        User byName = userRepository.findByName(userName);
        return (byName != null) ? byName : userRepository.save(new User(userName));
    }

    @Override
    public String addMoney(String userName, int countMoney) throws UserServiceException {
        validateCountMoney(countMoney);
        User user = getUser(userName);
        user.setMoney(user.getMoney() + countMoney);
        userRepository.save(user);
        return "Добавление денег выполнено успешно";
    }

    @Override
    public String getMoney(String userName, int countMoney) throws UserServiceException {
        validateCountMoney(countMoney);
        User byName = userRepository.findByName(userName);
        if (byName == null) {
            throw new UserServiceException("Пользователя с именем " + userName + " не существует");
        }
        if (byName.getMoney() < countMoney) {
            throw new UserServiceException("Запрашиваемая сумма больше, чем доступная для пользователя " + userName);
        }
        byName.setMoney(byName.getMoney() - countMoney);
        userRepository.save(byName);
        return "Снятие денег выполнено успешно";
    }

    private static void validateCountMoney(int countMoney) throws UserServiceException {
        if (countMoney <= 0) {
            throw new UserServiceException("Сумма должна быть больше 0");
        }
    }
}
