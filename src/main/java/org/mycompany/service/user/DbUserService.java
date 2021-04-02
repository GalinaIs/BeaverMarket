package org.mycompany.service.user;

import org.mycompany.entity.User;
import org.mycompany.repository.UserRepository;
import org.springframework.stereotype.Component;

@Component
public class DbUserService implements UserService {
    private final UserRepository userRepository;

    public DbUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User getUser(String name) {
        User byName = userRepository.findByName(name);
        return (byName != null) ? byName : userRepository.save(new User(name));
    }
}
