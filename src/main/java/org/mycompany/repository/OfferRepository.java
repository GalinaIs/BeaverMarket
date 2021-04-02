package org.mycompany.repository;

import org.mycompany.entity.Offer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OfferRepository extends JpaRepository<Offer, Long> {
    @Query("SELECT offer FROM Offer offer WHERE price >= :price AND type IS 'BUY' AND available_count > 0 AND user_id <> :userId")
    List<Offer> findBuyOffersByPriceMoreThanEqual(int price, long userId);

    @Query("SELECT offer FROM Offer offer WHERE price <= :price AND type IS 'SELL' AND available_count > 0 AND user_id <> :userId")
    List<Offer> findSellOffersByPriceLessThanEqual(int price, long userId);
}
