package org.mycompany.controller.advice;

import org.mycompany.service.exception.MarkerServiceException;
import org.mycompany.service.exception.UserServiceException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class NotValidDataAdvice {
    @ResponseBody
    @ExceptionHandler(MarkerServiceException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String clientNotFoundHandler(MarkerServiceException ex) {
        return ex.getMessage();
    }

    @ResponseBody
    @ExceptionHandler(UserServiceException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String userServiceException(UserServiceException ex) {
        return ex.getMessage();
    }
}
