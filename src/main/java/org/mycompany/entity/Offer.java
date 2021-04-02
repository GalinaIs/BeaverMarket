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
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;
    @Enumerated(EnumType.STRING)
    private OfferType type;

    public Offer() {

    }

    public Offer(int count, int price, int availableCount, OfferType type) {
        this.count = count;
        this.price = price;
        this.availableCount = availableCount;
        this.type = type;
    }
}
