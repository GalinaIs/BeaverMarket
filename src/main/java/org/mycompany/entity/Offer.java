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
    @Enumerated(EnumType.STRING)
    private OfferType type;

    public Offer() {

    }

    public Offer(int count, int price, OfferType type) {
        this.count = count;
        this.price = price;
        this.type = type;
    }
}
