package org.mycompany.controller;

import org.junit.jupiter.api.Test;
import org.mycompany.service.exception.UserServiceException;
import org.mycompany.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserService service;

    @Test
    public void addMoneySuccess() throws Exception {
        String userName = "name";
        int countMoney = 100;
        String addInformation = "addInformation";
        when(service.addMoney(userName, countMoney)).thenReturn(addInformation);

        this.mockMvc.perform(get("/addMoney")
                .param("userName", userName)
                .param("countMoney", String.valueOf(countMoney)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(addInformation)));
    }

    @Test
    public void addMoneyFail() throws Exception {
        String userName = "name";
        int countMoney = 100;
        String errorInformation = "errorInformation";
        when(service.addMoney(userName, countMoney)).thenThrow(new UserServiceException(errorInformation));

        this.mockMvc.perform(get("/addMoney")
                .param("userName", userName)
                .param("countMoney", String.valueOf(countMoney)))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString(errorInformation)));
    }

    @Test
    public void getMoneySuccess() throws Exception {
        String userName = "name";
        int countMoney = 100;
        String getInformation = "getInformation";
        when(service.getMoney(userName, countMoney)).thenReturn(getInformation);

        this.mockMvc.perform(get("/getMoney")
                .param("userName", userName)
                .param("countMoney", String.valueOf(countMoney)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(getInformation)));
    }

    @Test
    public void getMoneyFail() throws Exception {
        String userName = "name";
        int countMoney = 100;
        String errorInformation = "errorInformation";
        when(service.getMoney(userName, countMoney)).thenThrow(new UserServiceException(errorInformation));

        this.mockMvc.perform(get("/getMoney")
                .param("userName", userName)
                .param("countMoney", String.valueOf(countMoney)))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString(errorInformation)));
    }

    @Test
    public void unknownMethod() throws Exception {
        this.mockMvc.perform(get("/unknown"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }
}