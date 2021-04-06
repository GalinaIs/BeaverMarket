package org.mycompany.service.user;

import org.mycompany.entity.Offer;
import org.mycompany.entity.User;
import org.mycompany.repository.UserRepository;
import org.mycompany.service.exception.UserServiceException;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Service
public class DbUserService implements UserService {
    private final UserRepository userRepository;
    private final Map<Long, User> users = new ConcurrentHashMap<>();
    private final Executor executor = Executors.newCachedThreadPool();

    public DbUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostConstruct
    public void initUsers() {
        userRepository.findAll().forEach(user -> users.put(user.getId(), user));
    }

    @Override
    public User getUser(String userName) {
        User byName = findByName(userName);
        if (byName != null) {
            return byName;
        }
        User user = userRepository.save(new User(userName));
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public String addMoney(String userName, int countMoney) throws UserServiceException {
        validateCountMoney(countMoney);
        User user = getUser(userName);
        synchronized (user) {
            user.setMoney(user.getMoney() + countMoney);
        }
        executor.execute(() -> userRepository.save(user));
        return "Добавление денег выполнено успешно";
    }

    @Override
    public String getMoney(String userName, int countMoney) throws UserServiceException {
        validateCountMoney(countMoney);
        User byName = findByName(userName);
        if (byName == null) {
            throw new UserServiceException("Пользователя с именем " + userName + " не существует");
        }
        if (byName.getMoney() < countMoney) {
            throw new UserServiceException("Запрашиваемая сумма больше, чем доступная для пользователя " + userName);
        }
        synchronized (byName) {
            byName.setMoney(byName.getMoney() - countMoney);
        }
        executor.execute(() -> userRepository.save(byName));
        return "Снятие денег выполнено успешно";
    }

    public User getUser(Long userId) {
        return users.get(userId);
    }

    @Override
    public void saveAllUsers(Set<Offer> offers, Offer offer) {
        Set<User> usersSet = offers.stream()
                .map(off -> users.get(off.getUserId()).copy())
                .collect(Collectors.toSet());
        usersSet.add(getUser(offer.getUserId()));
        executor.execute(() -> userRepository.saveAll(usersSet));
    }

    private User findByName(String userName) {
        Optional<User> byName = users.values().stream()
                .filter(user -> user.getName().equals(userName))
                .findAny();
        return byName.orElse(null);
    }

    private static void validateCountMoney(int countMoney) throws UserServiceException {
        if (countMoney <= 0) {
            throw new UserServiceException("Сумма должна быть больше 0");
        }
    }
}
