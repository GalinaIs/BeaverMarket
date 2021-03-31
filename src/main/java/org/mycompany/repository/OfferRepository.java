package org.mycompany.repository;

import org.mycompany.entity.Offer;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import java.util.List;

public interface OfferRepository extends Repository<Offer, Long> {
    @Query("SELECT offer FROM Offer offer WHERE price >= :price AND type IS 'BUY'")
    List<Offer> findBuyOffersByPriceMoreThanEqual(int price);

    @Query("SELECT offer FROM Offer offer WHERE price <= :price AND type IS 'SELL'")
    List<Offer> findSellOffersByPriceLessThanEqual(int price);

    Offer save(Offer offer);

    void delete(Offer entity);
}
