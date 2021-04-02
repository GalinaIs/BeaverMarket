package org.mycompany.service;

import org.mycompany.entity.Offer;
import org.mycompany.entity.OfferType;
import org.mycompany.entity.User;
import org.mycompany.repository.OfferRepository;
import org.mycompany.service.exception.MarkerServiceException;
import org.mycompany.service.transaction.TransactionService;
import org.mycompany.service.user.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(isolation = Isolation.REPEATABLE_READ)
public class DbMarketService implements MarketService {
    private final OfferRepository offerRepository;
    private final TransactionService transactionService;
    private final UserService userService;

    public DbMarketService(OfferRepository offerRepository, TransactionService transactionService, UserService userService) {
        this.offerRepository = offerRepository;
        this.transactionService = transactionService;
        this.userService = userService;
    }

    @Override
    public String trySell(int count, int price, String userName) throws MarkerServiceException {
        validateData(count, price);
        User user = userService.getUser(userName);
        Offer newOffer = new Offer(count, price, count, user, OfferType.SELL);
        List<Offer> offers = offerRepository.findBuyOffersByPriceMoreThanEqual(price, user.getId());
        return offers.isEmpty() ? saveSellOffer(newOffer) : sell(newOffer, offers);
    }

    @Override
    public String tryBuy(int count, int price, String userName) throws MarkerServiceException {
        validateData(count, price);
        User user = userService.getUser(userName);
        Offer newOffer = new Offer(count, price, count, user, OfferType.BUY);
        List<Offer> offers = offerRepository.findSellOffersByPriceLessThanEqual(price, user.getId());
        return offers.isEmpty() ? saveBuyOffer(newOffer) : buy(newOffer, offers);
    }

    private void validateData(int count, int price) throws MarkerServiceException {
        if (count <= 0 || price <= 0) {
            throw new MarkerServiceException("Заявка не может быть исполнена, т.к. количество и цена должны быть больше 0");
        }
    }

    private String saveSellOffer(Offer offer) {
        offerRepository.save(offer);
        return "Выставлена заявка на продажу бобров";
    }

    private String saveBuyOffer(Offer offer) {
        offerRepository.save(offer);
        return "Выставлена заявка на покупку бобров";
    }

    private String sell(Offer newOffer, List<Offer> offers) {
        makeTransaction(newOffer, offers);
        if (newOffer.getAvailableCount() == 0) {
            return "Все бобры проданы";
        }
        if (newOffer.getAvailableCount() == newOffer.getCount()) {
            return "Выставлена заявка на продажу бобров";
        }
        return String.format("Продано %s бобров. Выставлена заявка на продажу %s бобров", newOffer.getCount() - newOffer.getAvailableCount(), newOffer.getAvailableCount());

    }

    private String buy(Offer newOffer, List<Offer> offers) {
        makeTransaction(newOffer, offers);
        if (newOffer.getAvailableCount() == 0) {
            return "Все бобры куплены";
        }
        if (newOffer.getAvailableCount() == newOffer.getCount()) {
            return "Выставлена заявка на покупку бобров";
        }
        return String.format("Куплено %s бобров. Выставлена заявка на покупку %s бобров", newOffer.getCount() - newOffer.getAvailableCount(), newOffer.getAvailableCount());
    }

    private void makeTransaction(Offer newOffer, List<Offer> offers) {
        transactionService.transactionProcess(offers, newOffer);
        offerRepository.saveAll(offers);
        offerRepository.save(newOffer);
    }
}
