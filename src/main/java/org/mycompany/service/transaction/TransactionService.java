package org.mycompany.service.transaction;

import org.mycompany.entity.Offer;

import java.util.List;

public interface TransactionService {
    void transactionProcess(List<Offer> offers, Offer offer);
}
