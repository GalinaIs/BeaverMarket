package org.mycompany.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class Offer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int count;
    private int price;
    private int availableCount;
    @Column(name="user_id")
    private Long userId;
    @Enumerated(EnumType.STRING)
    private OfferType type;

    public Offer() {
    }

    public Offer(int count, int price, int availableCount, Long userId, OfferType type) {
        this.count = count;
        this.price = price;
        this.availableCount = availableCount;
        this.userId = userId;
        this.type = type;
    }

    synchronized public Offer copy() {
        Offer offer = new Offer(count, price, availableCount, userId, type);
        offer.id = id;
        return offer;
    }
}
