package org.mycompany.controller;

import org.mycompany.service.MarketService;
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
    public String sellBeaver(@RequestParam int count, @RequestParam int price) {
        return marketService.trySell(count, price);
    }

    @GetMapping("/buy")
    public String buyBeaver(@RequestParam int count, @RequestParam int price) {
        return marketService.tryBuy(count, price);
    }
}
