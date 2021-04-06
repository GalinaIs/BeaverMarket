package org.mycompany.service;

import org.mycompany.entity.Offer;
import org.mycompany.entity.OfferType;
import org.mycompany.entity.User;
import org.mycompany.service.exception.MarkerServiceException;
import org.mycompany.service.offer.OfferService;
import org.mycompany.service.user.UserService;
import org.springframework.stereotype.Service;

@Service
public class DbMarketService implements MarketService {
    private final OfferService offerService;
    private final UserService userService;

    public DbMarketService(OfferService offerService, UserService userService) {
        this.offerService = offerService;
        this.userService = userService;
    }

    @Override
    public String trySell(int count, int price, String userName) throws MarkerServiceException {
        validateData(count, price);
        User user = userService.getUser(userName);
        Offer newOffer = new Offer(count, price, count, user.getId(), OfferType.SELL);
        return offerService.trySell(newOffer, user);
    }

    @Override
    public String tryBuy(int count, int price, String userName) throws MarkerServiceException {
        validateData(count, price);
        User user = userService.getUser(userName);
        Offer newOffer = new Offer(count, price, count, user.getId(), OfferType.BUY);
        return offerService.tryBuy(newOffer, user);
    }

    private void validateData(int count, int price) throws MarkerServiceException {
        if (count <= 0 || price <= 0) {
            throw new MarkerServiceException("Заявка не может быть исполнена, т.к. количество и цена должны быть больше 0");
        }
    }
}
