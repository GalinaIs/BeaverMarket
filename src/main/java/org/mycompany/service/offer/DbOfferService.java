package org.mycompany.service.offer;

import org.mycompany.entity.Deal;
import org.mycompany.entity.Offer;
import org.mycompany.entity.OfferType;
import org.mycompany.entity.User;
import org.mycompany.repository.DealRepository;
import org.mycompany.repository.OfferRepository;
import org.mycompany.service.offer.comparator.BuyOfferComparator;
import org.mycompany.service.offer.comparator.SellOfferComparator;
import org.mycompany.service.transaction.TransactionService;
import org.mycompany.service.user.UserService;
import org.mycompany.service.util.ExecuteUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.NavigableSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

@Service
public class DbOfferService implements OfferService {
    private final OfferRepository offerRepository;
    private final UserService userService;
    private final TransactionService transactionService;
    private final DealRepository dealRepository;
    private final NavigableSet<Offer> sellOffers = new ConcurrentSkipListSet<>(new SellOfferComparator());
    private final NavigableSet<Offer> buyOffers = new ConcurrentSkipListSet<>(new BuyOfferComparator());
    private final Executor executor;

    @Autowired
    public DbOfferService(OfferRepository offerRepository, UserService userService, TransactionService transactionService, DealRepository dealRepository, Executor executor) {
        this.offerRepository = offerRepository;
        this.userService = userService;
        this.transactionService = transactionService;
        this.dealRepository = dealRepository;
        this.executor = executor;
    }

    public DbOfferService(OfferRepository offerRepository, UserService userService, TransactionService transactionService, DealRepository dealRepository) {
        this(offerRepository, userService, transactionService, dealRepository, null);
    }

    @PostConstruct
    public void initSellBuyOffers() {
        buyOffers.addAll(offerRepository.findBuyOffers());
        sellOffers.addAll(offerRepository.findSellOffers());
    }

    @Override
    public String trySell(Offer newOffer, User newOfferUser) {
        Set<Offer> offers = buyOffers.headSet(newOffer, true);
        offers = offers.stream()
                .filter(offer -> {
                    User user = userService.getUser(offer.getUserId());
                    return !offer.getUserId().equals(newOfferUser.getId()) && user.getMoney() >= offer.getPrice();
                })
                .collect(Collectors.toCollection(() -> new TreeSet<>(new BuyOfferComparator())));
        return offers.isEmpty() ? saveSellOffer(newOffer) : sell(newOffer, offers);
    }

    @Override
    public String tryBuy(Offer newOffer, User newOfferUser) {
        Set<Offer> offers = sellOffers.headSet(newOffer, true);
        offers = offers.stream()
                .filter(offer -> !offer.getUserId().equals(newOfferUser.getId()) && newOfferUser.getMoney() >= offer.getPrice())
                .collect(Collectors.toCollection(() -> new TreeSet<>(new SellOfferComparator())));
        return offers.isEmpty() ? saveBuyOffer(newOffer) : buy(newOffer, offers);
    }

    private String saveSellOffer(Offer offer) {
        ExecuteUtils.execute(executor, () -> {
            addOfferInDbAndSet(offer);
        });
        return "Выставлена заявка на продажу бобров";
    }

    private String saveBuyOffer(Offer offer) {
        ExecuteUtils.execute(executor, () -> {
            addOfferInDbAndSet(offer);
        });
        return "Выставлена заявка на покупку бобров";
    }

    private String sell(Offer newOffer, Set<Offer> offers) {
        makeTransaction(newOffer, offers);
        if (newOffer.getAvailableCount() == 0) {
            return "Все бобры проданы";
        }
        if (newOffer.getAvailableCount() == newOffer.getCount()) {
            return "Выставлена заявка на продажу бобров";
        }
        return String.format("Продано %s бобров. Выставлена заявка на продажу %s бобров", newOffer.getCount() - newOffer.getAvailableCount(), newOffer.getAvailableCount());

    }

    private String buy(Offer newOffer, Set<Offer> offers) {
        makeTransaction(newOffer, offers);
        if (newOffer.getAvailableCount() == 0) {
            return "Все бобры куплены";
        }
        if (newOffer.getAvailableCount() == newOffer.getCount()) {
            return "Выставлена заявка на покупку бобров";
        }
        return String.format("Куплено %s бобров. Выставлена заявка на покупку %s бобров", newOffer.getCount() - newOffer.getAvailableCount(), newOffer.getAvailableCount());
    }

    private void makeTransaction(Offer newOffer, Set<Offer> offers) {
        List<Deal> deals = transactionService.transactionProcess(offers, newOffer);
        ExecuteUtils.execute(executor, () -> {
            offers.stream().filter(of -> of.getAvailableCount() == 0)
                    .forEach(of -> {
                        if (of.getType() == OfferType.BUY) {
                            buyOffers.remove(of);
                        } else {
                            sellOffers.remove(of);
                        }
                    });
            List<Offer> copyOffers = offers.stream().map(Offer::copy).collect(Collectors.toList());
            offerRepository.saveAll(copyOffers);
            addOfferInDbAndSet(newOffer);
            dealRepository.saveAll(deals);
        });
    }

    private void addOfferInDbAndSet(Offer offer) {
        offer = offerRepository.save(offer);
        if (offer.getAvailableCount() > 0) {
            if (offer.getType() == OfferType.BUY) {
                buyOffers.add(offer);
            } else {
                sellOffers.add(offer);
            }
        }
    }
}
