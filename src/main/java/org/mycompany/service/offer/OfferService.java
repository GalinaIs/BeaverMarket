package org.mycompany.service.offer;

import org.mycompany.entity.Offer;
import org.mycompany.entity.User;

public interface OfferService {
    String trySell(Offer newOffer, User user);

    String tryBuy(Offer newOffer, User user);
}
