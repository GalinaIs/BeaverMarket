package org.mycompany.service.transaction;

import org.mycompany.entity.Deal;
import org.mycompany.entity.Offer;
import org.mycompany.entity.OfferType;
import org.mycompany.entity.Transaction;
import org.mycompany.repository.DealRepository;
import org.mycompany.repository.TransactionRepository;
import org.mycompany.service.user.UserService;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Component
public class DbTransactionService implements TransactionService {
    private final DealRepository dealRepository;
    private final TransactionRepository transactionRepository;
    private final UserService userService;

    public DbTransactionService(DealRepository dealRepository, TransactionRepository transactionRepository, UserService userService) {
        this.dealRepository = dealRepository;
        this.transactionRepository = transactionRepository;
        this.userService = userService;
    }

    public void transactionProcess(List<Offer> offers, Offer offer) {
        Transaction transaction = new Transaction(new Timestamp(System.currentTimeMillis()));
        transactionRepository.save(transaction);
        dealRepository.saveAll(dealProcessing(offers, offer, transaction));
        userService.saveAllUsers(offers, offer);
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
            if (newDeal != null) {
                deals.add(newDeal);
            }
        }
        return deals;
    }

    private Deal createNewDeal(Offer buyOffer, Offer sellOffer, int price, Transaction transaction) {
        int dealCount = Math.min(buyOffer.getAvailableCount(), sellOffer.getAvailableCount());
        int countFromBuyOfferUser = buyOffer.getUser().getMoney() / price;
        if (countFromBuyOfferUser == 0) {
            return null;
        }
        dealCount = Math.min(dealCount, countFromBuyOfferUser);
        buyOffer.setAvailableCount(buyOffer.getAvailableCount() - dealCount);
        sellOffer.setAvailableCount(sellOffer.getAvailableCount() - dealCount);
        int sumDeal = dealCount * price;
        buyOffer.getUser().setMoney(buyOffer.getUser().getMoney() - sumDeal);
        sellOffer.getUser().setMoney(sellOffer.getUser().getMoney() + sumDeal);
        return new Deal(buyOffer, sellOffer, dealCount, price, transaction);
    }
}
