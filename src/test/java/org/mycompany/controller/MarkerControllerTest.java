package org.mycompany.controller;

import org.junit.jupiter.api.Test;
import org.mycompany.service.MarketService;
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

@WebMvcTest(MarkerController.class)
public class MarkerControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private MarketService service;

    @Test
    public void buyShouldReturnOfferInformation() throws Exception {
        int count = 2;
        int price = 100;
        String offerInformation = "offerInformation";
        when(service.tryBuy(count, price)).thenReturn(offerInformation);

        this.mockMvc.perform(get("/buy")
                    .param("count", String.valueOf(count))
                    .param("price", String.valueOf(price)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(offerInformation)));
    }

    @Test
    public void sellShouldReturnOfferInformation() throws Exception {
        int count = 2;
        int price = 100;
        String offerInformation = "offerInformation";
        when(service.trySell(count, price)).thenReturn(offerInformation);

        this.mockMvc.perform(get("/sell")
                .param("count", String.valueOf(count))
                .param("price", String.valueOf(price)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(offerInformation)));
    }

    @Test
    public void unknownMethod() throws Exception {
        this.mockMvc.perform(get("/unknown"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }
}