package org.mycompany.service;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mycompany.entity.Deal;
import org.mycompany.entity.User;
import org.mycompany.repository.DealRepository;
import org.mycompany.repository.OfferRepository;
import org.mycompany.repository.TransactionRepository;
import org.mycompany.repository.UserRepository;
import org.mycompany.service.exception.MarkerServiceException;
import org.mycompany.service.transaction.DbTransactionService;
import org.mycompany.service.transaction.TransactionService;
import org.mycompany.service.user.DbUserService;
import org.mycompany.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
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
    private static final String USER1 = "user1";
    private static final String USER2 = "user2";
    private static final String USER3 = "user3";
    private static final String USER4 = "user4";

    @Autowired
    OfferRepository offerRepository;
    @Autowired
    TransactionRepository transactionRepository;
    @Autowired
    DealRepository dealRepository;
    @Autowired
    UserRepository userRepository;
    private UserService userService;
    private DbMarketService marketService;

    @BeforeEach
    public void setUp() {
        userService = new DbUserService(userRepository);
        TransactionService transactionService = new DbTransactionService(dealRepository, transactionRepository, userService);
        marketService = new DbMarketService(offerRepository, transactionService, userService);
    }

    @Test
    void tryBuyAndSellTheSameQuality() throws MarkerServiceException {
        User user1 = userService.getUser(USER1);
        user1.setMoney(100);
        Assert.assertEquals("Выставлена заявка на покупку бобров", marketService.tryBuy(1, 100, USER1));
        Assert.assertEquals("Все бобры проданы", marketService.trySell(1, 100, USER2));
        List<Deal> deals = dealRepository.findAll();
        Assert.assertEquals(1, deals.size());
        assertDeal(deals.get(0), 100, 100, 1, 100);
        Assert.assertEquals(0, user1.getMoney());
        Assert.assertEquals(100, userService.getUser(USER2).getMoney());
    }

    @Test
    void tryBuyMoreQualityThanSell() throws MarkerServiceException {
        User user1 = userService.getUser(USER1);
        user1.setMoney(110);
        Assert.assertEquals("Выставлена заявка на покупку бобров", marketService.tryBuy(2, 110, USER1));
        Assert.assertEquals("Все бобры проданы", marketService.trySell(1, 100, USER2));
        List<Deal> deals = dealRepository.findAll();
        Assert.assertEquals(1, deals.size());
        assertDeal(deals.get(0), 110, 100, 1, 110);
        Assert.assertEquals(0, user1.getMoney());
        Assert.assertEquals(110, userService.getUser(USER2).getMoney());
    }

    @Test
    void tryBuyLessQualityThanSell() throws MarkerServiceException {
        User user1 = userService.getUser(USER1);
        user1.setMoney(3 * 110);
        Assert.assertEquals("Выставлена заявка на покупку бобров", marketService.tryBuy(3, 110, USER1));
        Assert.assertEquals("Продано 3 бобров. Выставлена заявка на продажу 2 бобров", marketService.trySell(5, 100, USER2));
        List<Deal> deals = dealRepository.findAll();
        Assert.assertEquals(1, deals.size());
        assertDeal(deals.get(0), 110, 100, 3, 110);
        Assert.assertEquals(0, user1.getMoney());
        Assert.assertEquals(3 * 110, userService.getUser(USER2).getMoney());
    }

    @Test
    void severalOffersForBuy() throws MarkerServiceException {
        User user1 = userService.getUser(USER1);
        user1.setMoney(3 * 110);
        User user3 = userService.getUser(USER3);
        user3.setMoney(2 * 100);
        Assert.assertEquals("Выставлена заявка на покупку бобров", marketService.tryBuy(3, 110, USER1));
        Assert.assertEquals("Продано 3 бобров. Выставлена заявка на продажу 2 бобров", marketService.trySell(5, 100, USER2));
        Assert.assertEquals("Все бобры куплены", marketService.tryBuy(2, 105, USER3));
        List<Deal> deals = dealRepository.findAll();
        Assert.assertEquals(2, deals.size());
        assertDeal(deals.get(0), 110, 100, 3, 110);
        assertDeal(deals.get(1), 105, 100, 2, 100);
        Assert.assertEquals(0, user1.getMoney());
        Assert.assertEquals(0, user3.getMoney());
        Assert.assertEquals(3 * 110 + 2 * 100, userService.getUser(USER2).getMoney());
    }

    @Test
    void severalOffersForSell() throws MarkerServiceException {
        User user1 = userService.getUser(USER1);
        user1.setMoney(5 * 105);
        Assert.assertEquals("Выставлена заявка на покупку бобров", marketService.tryBuy(5, 105, USER1));
        Assert.assertEquals("Все бобры проданы", marketService.trySell(2, 100, USER2));
        Assert.assertEquals("Все бобры проданы", marketService.trySell(3, 90, USER3));
        List<Deal> deals = dealRepository.findAll();
        Assert.assertEquals(2, deals.size());
        assertDeal(deals.get(0), 105, 100, 2, 105);
        assertDeal(deals.get(1), 105, 90, 3, 105);
        Assert.assertEquals(0, user1.getMoney());
        Assert.assertEquals(2 * 105, userService.getUser(USER2).getMoney());
        Assert.assertEquals(3 * 105, userService.getUser(USER3).getMoney());
    }

    @Test
    void severalOffersForBuyAndSell() throws MarkerServiceException {
        User user3 = userService.getUser(USER3);
        user3.setMoney(4 * 100);
        User user4 = userService.getUser(USER4);
        user4.setMoney(3 * 100);
        Assert.assertEquals("Выставлена заявка на продажу бобров", marketService.trySell(3, 100, USER1));
        Assert.assertEquals("Выставлена заявка на продажу бобров", marketService.trySell(5, 100, USER2));
        Assert.assertEquals("Все бобры куплены", marketService.tryBuy(4, 105, USER3));
        Assert.assertEquals("Все бобры куплены", marketService.tryBuy(3, 110, USER4));
        List<Deal> deals = dealRepository.findAll();
        Assert.assertEquals(3, deals.size());
        assertDeal(deals.get(0), 105, 100, 3, 100);
        assertDeal(deals.get(1), 105, 100, 1, 100);
        assertDeal(deals.get(2), 110, 100, 3, 100);
        Assert.assertEquals(0, user3.getMoney());
        Assert.assertEquals(0, user4.getMoney());
        Assert.assertEquals(3 * 100, userService.getUser(USER1).getMoney());
        Assert.assertEquals(4 * 100, userService.getUser(USER2).getMoney());
    }

    @Test
    void notMakeDealWithTheSameUser() throws MarkerServiceException {
        User user1 = userService.getUser(USER1);
        user1.setMoney(3 * 100);
        Assert.assertEquals("Выставлена заявка на продажу бобров", marketService.trySell(3, 100, USER1));
        Assert.assertEquals("Выставлена заявка на покупку бобров", marketService.tryBuy(3, 100, USER1));
        List<Deal> deals = dealRepository.findAll();
        Assert.assertEquals(0, deals.size());
        Assert.assertEquals(3 * 100, user1.getMoney());
    }

    @Test
    void makeDealWithMoreAttractivePriceBuy() throws MarkerServiceException {
        User user2 = userService.getUser(USER2);
        user2.setMoney(2 * 90 + 100);
        Assert.assertEquals("Выставлена заявка на продажу бобров", marketService.trySell(2, 100, USER1));
        Assert.assertEquals("Выставлена заявка на продажу бобров", marketService.trySell(2, 90, USER3));
        Assert.assertEquals("Все бобры куплены", marketService.tryBuy(3, 100, USER2));
        List<Deal> deals = dealRepository.findAll();
        Assert.assertEquals(2, deals.size());
        assertDeal(deals.get(0), 100, 90, 2, 90);
        assertDeal(deals.get(1), 100, 100, 1, 100);
        Assert.assertEquals(0, user2.getMoney());
        Assert.assertEquals(100, userService.getUser(USER1).getMoney());
        Assert.assertEquals(2 * 90, userService.getUser(USER3).getMoney());
    }


    @Test
    void makeDealWithMoreAttractivePriceSell() throws MarkerServiceException {
        User user1 = userService.getUser(USER1);
        user1.setMoney(100);
        User user3 = userService.getUser(USER3);
        user3.setMoney(2 * 110);
        Assert.assertEquals("Выставлена заявка на покупку бобров", marketService.tryBuy(2, 100, USER1));
        Assert.assertEquals("Выставлена заявка на покупку бобров", marketService.tryBuy(2, 110, USER3));
        Assert.assertEquals("Все бобры проданы", marketService.trySell(3, 100, USER2));
        List<Deal> deals = dealRepository.findAll();
        Assert.assertEquals(2, deals.size());
        assertDeal(deals.get(0), 110, 100, 2, 110);
        assertDeal(deals.get(1), 100, 100, 1, 100);
        Assert.assertEquals(0, user1.getMoney());
        Assert.assertEquals(0, user3.getMoney());
        Assert.assertEquals(2 * 110 + 100, userService.getUser(USER2).getMoney());
    }

    @Test
    void noMakeDealWithoutMoney() throws MarkerServiceException {
        Assert.assertEquals("Выставлена заявка на покупку бобров", marketService.tryBuy(1, 100, USER1));
        Assert.assertEquals("Выставлена заявка на продажу бобров", marketService.trySell(1, 100, USER2));
        List<Deal> deals = dealRepository.findAll();
        Assert.assertEquals(0, deals.size());
    }

    @Test
    void noMakeDealWithNoEnoughMoney() throws MarkerServiceException {
        User user1 = userService.getUser(USER1);
        user1.setMoney(90);
        Assert.assertEquals("Выставлена заявка на покупку бобров", marketService.tryBuy(1, 100, USER1));
        Assert.assertEquals("Выставлена заявка на продажу бобров", marketService.trySell(1, 100, USER2));
        List<Deal> deals = dealRepository.findAll();
        Assert.assertEquals(0, deals.size());
    }

    private static void assertDeal(Deal deal, int buyPrice, int sellPrice, int count, int price) {
        Assert.assertEquals(buyPrice, deal.getBuyOffer().getPrice());
        Assert.assertEquals(sellPrice, deal.getSellOffer().getPrice());
        Assert.assertEquals(count, deal.getCount());
        Assert.assertEquals(price, deal.getPrice());
    }
}