package org.mycompany.service.user;

import org.mycompany.entity.Offer;
import org.mycompany.entity.User;
import org.mycompany.service.exception.UserServiceException;

import java.util.Set;

public interface UserService {
    User getUser(String userName);

    String addMoney(String userName, int countMoney) throws UserServiceException;

    String getMoney(String userName, int countMoney) throws UserServiceException;

    User getUser(Long userId);

    void saveAllUsers(Set<Offer> offers, Offer offer);
}
