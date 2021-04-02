package org.mycompany.controller;

import org.mycompany.service.MarketService;
import org.mycompany.service.exception.MarkerServiceException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MarkerController {
    private final MarketService marketService;

    public MarkerController(MarketService marketService) {
        this.marketService = marketService;
    }

    @GetMapping("/sell")
    public String sellBeaver(@RequestParam int count, @RequestParam int price, @RequestParam String userName) throws MarkerServiceException {
        return marketService.trySell(count, price, userName);
    }

    @GetMapping("/buy")
    public String buyBeaver(@RequestParam int count, @RequestParam int price, @RequestParam String userName) throws MarkerServiceException {
        return marketService.tryBuy(count, price, userName);
    }
}
