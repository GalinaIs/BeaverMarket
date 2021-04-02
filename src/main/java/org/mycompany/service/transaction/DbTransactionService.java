package org.mycompany.service.transaction;

import org.mycompany.entity.Deal;
import org.mycompany.entity.Offer;
import org.mycompany.entity.OfferType;
import org.mycompany.entity.Transaction;
import org.mycompany.repository.DealRepository;
import org.mycompany.repository.TransactionRepository;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Component
public class DbTransactionService implements TransactionService {
    private final DealRepository dealRepository;
    private final TransactionRepository transactionRepository;

    public DbTransactionService(DealRepository dealRepository, TransactionRepository transactionRepository) {
        this.dealRepository = dealRepository;
        this.transactionRepository = transactionRepository;
    }

    public void transactionProcess(List<Offer> offers, Offer offer) {
        Transaction transaction = new Transaction(new Timestamp(System.currentTimeMillis()));
        transactionRepository.save(transaction);
        dealRepository.saveAll(dealProcessing(offers, offer, transaction));
    }

    private List<Deal> dealProcessing(List<Offer> offers, Offer newOffer, Transaction transaction) {
        List<Deal> deals = new ArrayList<>();
        for (Offer offer : offers) {
            if (newOffer.getAvailableCount() == 0) {
                break;
            }
            Deal newDeal = newOffer.getType() == OfferType.BUY
                    ? createNewDeal(newOffer, offer, offer.getPrice(), transaction)
                    : createNewDeal(offer, newOffer, offer.getPrice(), transaction);
            deals.add(newDeal);
        }
        return deals;
    }

    private Deal createNewDeal(Offer buyOffer, Offer sellOffer, int price, Transaction transaction) {
        int dealCount = Math.min(buyOffer.getAvailableCount(), sellOffer.getAvailableCount());
        buyOffer.setAvailableCount(buyOffer.getAvailableCount() - dealCount);
        sellOffer.setAvailableCount(sellOffer.getAvailableCount() - dealCount);
        return new Deal(buyOffer, sellOffer, dealCount, price, transaction);
    }
}
