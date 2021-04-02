package org.mycompany.service;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mycompany.entity.Deal;
import org.mycompany.repository.DealRepository;
import org.mycompany.repository.OfferRepository;
import org.mycompany.repository.TransactionRepository;
import org.mycompany.service.exception.MarkerServiceException;
import org.mycompany.service.transaction.DbTransactionService;
import org.mycompany.service.transaction.TransactionService;
import org.mycompany.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

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
    @Autowired
    TransactionRepository transactionRepository;
    @Autowired
    DealRepository dealRepository;
    @MockBean
    private UserService userService;

    @Test
    void tryBuyAndSellTheSameQuality() throws MarkerServiceException {
        TransactionService transactionService = new DbTransactionService(dealRepository, transactionRepository);
        DbMarketService marketService = new DbMarketService(offerRepository, transactionService, userService);
        Assert.assertEquals("Выставлена заявка на покупку бобров", marketService.tryBuy(1, 100, "user1"));
        Assert.assertEquals("Все бобры проданы", marketService.trySell(1, 100, "user2"));
        List<Deal> deals = dealRepository.findAll();
        Assert.assertEquals(1, deals.size());
        assertDeal(deals.get(0), 100, 100, 1);
    }

    @Test
    void tryBuyMoreQualityThanSell() throws MarkerServiceException {
        TransactionService transactionService = new DbTransactionService(dealRepository, transactionRepository);
        DbMarketService marketService = new DbMarketService(offerRepository, transactionService, userService);
        Assert.assertEquals("Выставлена заявка на покупку бобров", marketService.tryBuy(2, 110, "user1"));
        Assert.assertEquals("Все бобры проданы", marketService.trySell(1, 100, "user2"));
        List<Deal> deals = dealRepository.findAll();
        Assert.assertEquals(1, deals.size());
        assertDeal(deals.get(0), 110, 100, 1);
    }

    @Test
    void tryBuyLessQualityThanSell() throws MarkerServiceException {
        TransactionService transactionService = new DbTransactionService(dealRepository, transactionRepository);
        DbMarketService marketService = new DbMarketService(offerRepository, transactionService, userService);
        Assert.assertEquals("Выставлена заявка на покупку бобров", marketService.tryBuy(3, 110, "user1"));
        Assert.assertEquals("Продано 3 бобров. Выставлена заявка на продажу 2 бобров", marketService.trySell(5, 100, "user2"));
        List<Deal> deals = dealRepository.findAll();
        Assert.assertEquals(1, deals.size());
        assertDeal(deals.get(0), 110, 100, 3);
    }

    @Test
    void severalOffersForBuy() throws MarkerServiceException {
        TransactionService transactionService = new DbTransactionService(dealRepository, transactionRepository);
        DbMarketService marketService = new DbMarketService(offerRepository, transactionService, userService);
        Assert.assertEquals("Выставлена заявка на покупку бобров", marketService.tryBuy(3, 110, "user1"));
        Assert.assertEquals("Продано 3 бобров. Выставлена заявка на продажу 2 бобров", marketService.trySell(5, 100, "user2"));
        Assert.assertEquals("Все бобры куплены", marketService.tryBuy(2, 105, "user3"));
        List<Deal> deals = dealRepository.findAll();
        Assert.assertEquals(2, deals.size());
        assertDeal(deals.get(0), 110, 100, 3);
        assertDeal(deals.get(1), 105, 100, 2);

    }

    @Test
    void severalOffersForSell() throws MarkerServiceException {
        TransactionService transactionService = new DbTransactionService(dealRepository, transactionRepository);
        DbMarketService marketService = new DbMarketService(offerRepository, transactionService, userService);
        Assert.assertEquals("Выставлена заявка на покупку бобров", marketService.tryBuy(5, 105, "user1"));
        Assert.assertEquals("Все бобры проданы", marketService.trySell(2, 100, "user2"));
        Assert.assertEquals("Все бобры проданы", marketService.trySell(3, 90, "user3"));
        List<Deal> deals = dealRepository.findAll();
        Assert.assertEquals(2, deals.size());
        assertDeal(deals.get(0), 105, 100, 2);
        assertDeal(deals.get(1), 105, 90, 3);
    }

    @Test
    void severalOffersForBuyAndSell() throws MarkerServiceException {
        TransactionService transactionService = new DbTransactionService(dealRepository, transactionRepository);
        DbMarketService marketService = new DbMarketService(offerRepository, transactionService, userService);
        Assert.assertEquals("Выставлена заявка на продажу бобров", marketService.trySell(3, 100, "user1"));
        Assert.assertEquals("Выставлена заявка на продажу бобров", marketService.trySell(5, 100, "user2"));
        Assert.assertEquals("Все бобры куплены", marketService.tryBuy(4, 105, "user3"));
        Assert.assertEquals("Все бобры куплены", marketService.tryBuy(3, 110, "user4"));
        List<Deal> deals = dealRepository.findAll();
        Assert.assertEquals(3, deals.size());
        assertDeal(deals.get(0), 105, 100, 3);
        assertDeal(deals.get(1), 105, 100, 1);
        assertDeal(deals.get(2), 110, 100, 3);
    }

    private static void assertDeal(Deal deal, int buyPrice, int sellPrice, int count) {
        Assert.assertEquals(buyPrice, deal.getBuyOffer().getPrice());
        Assert.assertEquals(sellPrice, deal.getSellOffer().getPrice());
        Assert.assertEquals(count, deal.getCount());
    }
}