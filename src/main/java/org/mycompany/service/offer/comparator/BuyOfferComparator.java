package org.mycompany.service.offer.comparator;

import org.mycompany.entity.Offer;

import java.util.Comparator;

public class BuyOfferComparator implements Comparator<Offer> {
    @Override
    public int compare(Offer offer1, Offer offer2) {
        int comparePrice = Integer.compare(offer1.getPrice(), offer2.getPrice());
        if (comparePrice != 0) {
            return -comparePrice;
        }
        if (offer1.getId() == null && offer2.getId() == null) {
            return 0;
        }
        if (offer1.getId() == null) {
            return 1;
        }
        if (offer2.getId() == null) {
            return -1;
        }
        return Long.compare(offer1.getId(), offer2.getId());
    }
}
