package org.mycompany.service;

import org.mycompany.entity.Offer;
import org.mycompany.entity.OfferType;
import org.mycompany.repository.OfferRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Component
@Transactional(isolation = Isolation.REPEATABLE_READ)
public class DbMarketService implements MarketService {
    private final OfferRepository offerRepository;

    public DbMarketService(OfferRepository offerRepository) {
        this.offerRepository = offerRepository;
    }

    @Override
    public String trySell(int count, int price) {
        List<Offer> offers = offerRepository.findBuyOffersByPriceMoreThanEqual(price);
        int countSum = offers.stream().mapToInt(Offer::getCount).sum();
        return countSum == 0 ? saveSellOffer(count, price) : sell(count, price, offers);
    }

    @Override
    public String tryBuy(int count, int price) {
        List<Offer> offers = offerRepository.findSellOffersByPriceLessThanEqual(price);
        int countSum = offers.stream().mapToInt(Offer::getCount).sum();
        return countSum == 0 ? saveBuyOffer(count, price) : buy(count, price, offers);
    }

    private String saveSellOffer(int count, int price) {
        Offer offer = new Offer(count, price, OfferType.SELL);
        offerRepository.save(offer);
        return "Выставлена заявка на продажу бобров";
    }

    private String saveBuyOffer(int count, int price) {
        Offer offer = new Offer(count, price, OfferType.BUY);
        offerRepository.save(offer);
        return "Выставлена заявка на покупку бобров";
    }

    private String sell(int count, int price, List<Offer> offers) {
        int sellCount = deleteOffersFromDb(count, offers);
        if (sellCount == count) {
            return "Все бобры проданы";
        }
        int remainingCount = count - sellCount;
        saveSellOffer(remainingCount, price);
        return String.format("Продано %s бобров. Выставлена заявка на продажу %s бобров", sellCount, remainingCount);
    }

    private String buy(int count, int price, List<Offer> offers) {
        int buyCount = deleteOffersFromDb(count, offers);
        if (buyCount == count) {
            return "Все бобры куплены";
        };
        int remainingCount = count - buyCount;
        saveBuyOffer(count, price);
        return String.format("Куплено %s бобров. Выставлена заявка на покупку %s бобров", buyCount, remainingCount);
    }

    private int deleteOffersFromDb(int count, List<Offer> offers) {
        int countCurrentOffer = 0;
        for (Offer offer : offers) {
            if (count == 0) {
                break;
            }
            if (count >= offer.getCount()) {
                offerRepository.delete(offer);
                count = count - offer.getCount();
                countCurrentOffer = countCurrentOffer + offer.getCount();
            } else {
                offer.setCount(offer.getCount() - count);
                offerRepository.save(offer);
                countCurrentOffer = countCurrentOffer + count;
                break;
            }
        }
        return countCurrentOffer;
    }
}
