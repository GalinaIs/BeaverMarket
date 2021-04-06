package org.mycompany.service.transaction;

import org.mycompany.entity.*;
import org.mycompany.repository.DealRepository;
import org.mycompany.repository.TransactionRepository;
import org.mycompany.service.user.UserService;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Service
public class DbTransactionService implements TransactionService {
    private final TransactionRepository transactionRepository;
    private final UserService userService;
    private final Executor executor = Executors.newCachedThreadPool();

    public DbTransactionService(TransactionRepository transactionRepository, UserService userService) {
        this.transactionRepository = transactionRepository;
        this.userService = userService;
    }

    public List<Deal> transactionProcess(Set<Offer> offers, Offer offer) {
        Transaction transaction = new Transaction(new Timestamp(System.currentTimeMillis()));
        transactionRepository.save(transaction);
        List<Deal> deals = dealProcessing(offers, offer, transaction);
        executor.execute(() -> {
            userService.saveAllUsers(offers, offer);
        });
        return deals;
    }

    private List<Deal> dealProcessing(Set<Offer> offers, Offer newOffer, Transaction transaction) {
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
        User buyOfferUser = userService.getUser(buyOffer.getUserId());
        User sellOfferUser = userService.getUser(sellOffer.getUserId());
        synchronized (buyOffer) {
            synchronized (sellOffer) {
                synchronized (buyOfferUser.getId() < sellOffer.getUserId() ? buyOffer : sellOffer) {
                    synchronized (buyOfferUser.getId() < sellOffer.getUserId() ? sellOffer : buyOffer) {
                        int dealCount = Math.min(buyOffer.getAvailableCount(), sellOffer.getAvailableCount());
                        int countFromBuyOfferUser = buyOfferUser.getMoney() / price;
                        if (countFromBuyOfferUser == 0) {
                            return null;
                        }
                        dealCount = Math.min(dealCount, countFromBuyOfferUser);
                        buyOffer.setAvailableCount(buyOffer.getAvailableCount() - dealCount);
                        sellOffer.setAvailableCount(sellOffer.getAvailableCount() - dealCount);
                        int sumDeal = dealCount * price;
                        buyOfferUser.setMoney(buyOfferUser.getMoney() - sumDeal);
                        sellOfferUser.setMoney(sellOfferUser.getMoney() + sumDeal);
                        return new Deal(buyOffer, sellOffer, dealCount, price, transaction);
                    }
                }
            }
        }
    }
}
