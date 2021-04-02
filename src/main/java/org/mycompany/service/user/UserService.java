package org.mycompany.service.user;

import org.mycompany.entity.User;
import org.mycompany.service.exception.UserServiceException;

public interface UserService {
    User getUser(String userName);

    String addMoney(String userName, int countMoney) throws UserServiceException;

    String getMoney(String userName, int countMoney) throws UserServiceException;
}
