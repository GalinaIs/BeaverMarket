package org.mycompany.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class Deal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "sell_offer_id", referencedColumnName = "id")
    private Offer buyOffer;
    @ManyToOne
    @JoinColumn(name = "buy_offer_id", referencedColumnName = "id")
    private Offer sellOffer;
    private int count;
    private int price;
    @ManyToOne
    @JoinColumn(name = "transaction_id", referencedColumnName = "id")
    private Transaction transaction;

    public Deal() {

    }

    public Deal(Offer buyOffer, Offer sellOffer, int count, int price, Transaction transaction) {
        this.buyOffer = buyOffer;
        this.sellOffer = sellOffer;
        this.count = count;
        this.price = price;
        this.transaction = transaction;
    }
}
