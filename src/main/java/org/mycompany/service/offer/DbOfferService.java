package org.mycompany.service.offer;

import org.mycompany.entity.Offer;
import org.mycompany.entity.User;
import org.mycompany.repository.OfferRepository;
import org.mycompany.service.transaction.TransactionService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DbOfferService implements OfferService {
    private final OfferRepository offerRepository;
    private final TransactionService transactionService;

    public DbOfferService(OfferRepository offerRepository, TransactionService transactionService) {
        this.offerRepository = offerRepository;
        this.transactionService = transactionService;
    }

    @Override
    public String trySell(Offer newOffer, User user) {
        List<Offer> offers = offerRepository.findBuyOffersByPriceMoreThanEqual(newOffer.getPrice(), user.getId());
        return offers.isEmpty() ? saveSellOffer(newOffer) : sell(newOffer, offers);
    }

    @Override
    public String tryBuy(Offer newOffer, User user) {
        List<Offer> offers = offerRepository.findSellOffersByPriceLessThanEqual(newOffer.getPrice(), user.getId());
        return offers.isEmpty() ? saveBuyOffer(newOffer) : buy(newOffer, offers);
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
