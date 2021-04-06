package org.mycompany.repository;

import org.mycompany.entity.Offer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OfferRepository extends JpaRepository<Offer, Long> {
    @Query("SELECT offer FROM Offer offer WHERE type IS 'SELL' AND available_count > 0")
    List<Offer> findSellOffers();

    @Query("SELECT offer FROM Offer offer WHERE type IS 'BUY' AND available_count > 0")
    List<Offer> findBuyOffers();
}
