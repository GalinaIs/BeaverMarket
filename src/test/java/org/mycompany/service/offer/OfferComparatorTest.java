package org.mycompany.service.offer;

import org.junit.Assert;
import org.junit.Test;
import org.mycompany.entity.Offer;
import org.mycompany.entity.OfferType;
import org.mycompany.service.offer.comparator.BuyOfferComparator;
import org.mycompany.service.offer.comparator.SellOfferComparator;

import java.util.NavigableSet;
import java.util.concurrent.ConcurrentSkipListSet;

public class OfferComparatorTest {
    private static long id = 0;

    @Test
    public void testHeadSetSellOffers() {
        NavigableSet<Offer> sellOffers = new ConcurrentSkipListSet<>(new SellOfferComparator());
        sellOffers.add(createSellOffer(100));
        sellOffers.add(createSellOffer(80));
        sellOffers.add(createSellOffer(90));
        sellOffers.add(createSellOffer(110));
        sellOffers.add(createSellOffer(130));
        sellOffers.add(createSellOffer(120));
        NavigableSet<Offer> offers = sellOffers.headSet(new Offer(1, 110, 1, 1L, OfferType.BUY), true);
        Assert.assertEquals(4, offers.size());
        assertOffer(offers.pollFirst(), 80);
        assertOffer(offers.pollFirst(), 90);
        assertOffer(offers.pollFirst(), 100);
        assertOffer(offers.pollFirst(), 110);
    }

    @Test
    public void testHeadSetBuyOffers() {
        NavigableSet<Offer> buyOffers = new ConcurrentSkipListSet<>(new BuyOfferComparator());
        buyOffers.add(createBuyOffer(100));
        buyOffers.add(createBuyOffer(80));
        buyOffers.add(createBuyOffer(90));
        buyOffers.add(createBuyOffer(110));
        buyOffers.add(createBuyOffer(120));
        buyOffers.add(createBuyOffer(130));
        NavigableSet<Offer> offers = buyOffers.headSet(new Offer(1, 110, 1, 1L, OfferType.SELL), true);
        Assert.assertEquals(3, offers.size());
        assertOffer(offers.pollFirst(), 130);
        assertOffer(offers.pollFirst(), 120);
        assertOffer(offers.pollFirst(), 110);
    }

    private static Offer createSellOffer(int price) {
        Offer offer = new Offer(1, price, 1, 1L, OfferType.SELL);
        offer.setId(++id);
        return offer;
    }

    private static Offer createBuyOffer(int price) {
        Offer offer = new Offer(1, price, 1, 1L, OfferType.BUY);
        offer.setId(++id);
        return offer;
    }

    private static void assertOffer(Offer offer, int price) {
        Assert.assertEquals(price, offer.getPrice());
    }

}