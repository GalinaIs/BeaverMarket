package org.mycompany.service;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mycompany.repository.OfferRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@TestPropertySource(properties = {
        "spring.main.banner-mode=off",
        "spring.datasource.platform=h2",
        "spring.jpa.hibernate.ddl-auto=none"
})
public class DbMarketServiceTest {
    @Autowired
    OfferRepository offerRepository;

    @Test
    void tryBuyAndSellTheSameQuality() {
        DbMarketService marketService = new DbMarketService(offerRepository);
        Assert.assertEquals("Выставлена заявка на покупку бобров", marketService.tryBuy(1, 100));
        Assert.assertEquals("Все бобры проданы", marketService.trySell(1, 100));
    }

    @Test
    void tryBuyMoreQualityThanSell() {
        DbMarketService marketService = new DbMarketService(offerRepository);
        Assert.assertEquals("Выставлена заявка на покупку бобров", marketService.tryBuy(2, 110));
        Assert.assertEquals("Все бобры проданы", marketService.trySell(1, 100));
    }

    @Test
    void tryBuyLessQualityThanSell() {
        DbMarketService marketService = new DbMarketService(offerRepository);
        Assert.assertEquals("Выставлена заявка на покупку бобров", marketService.tryBuy(3, 110));
        Assert.assertEquals("Продано 3 бобров. Выставлена заявка на продажу 2 бобров", marketService.trySell(5, 100));
    }

    @Test
    void severalOffersForBuy() {
        DbMarketService marketService = new DbMarketService(offerRepository);
        Assert.assertEquals("Выставлена заявка на покупку бобров", marketService.tryBuy(3, 110));
        Assert.assertEquals("Продано 3 бобров. Выставлена заявка на продажу 2 бобров", marketService.trySell(5, 100));
        Assert.assertEquals("Все бобры куплены", marketService.tryBuy(2, 105));
    }

    @Test
    void severalOffersForSell() {
        DbMarketService marketService = new DbMarketService(offerRepository);
        Assert.assertEquals("Выставлена заявка на покупку бобров", marketService.tryBuy(5, 105));
        Assert.assertEquals("Все бобры проданы", marketService.trySell(2, 100));
        Assert.assertEquals("Все бобры проданы", marketService.trySell(3, 90));
    }

    @Test
    void severalOffersForBuyAndSell() {
        DbMarketService marketService = new DbMarketService(offerRepository);
        Assert.assertEquals("Выставлена заявка на продажу бобров", marketService.trySell(3, 100));
        Assert.assertEquals("Выставлена заявка на продажу бобров", marketService.trySell(5, 100));
        Assert.assertEquals("Все бобры куплены", marketService.tryBuy(4, 105));
        Assert.assertEquals("Все бобры куплены", marketService.tryBuy(3, 110));
    }

}