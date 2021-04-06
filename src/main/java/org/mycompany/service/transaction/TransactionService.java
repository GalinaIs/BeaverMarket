package org.mycompany.service.transaction;

import org.mycompany.entity.Deal;
import org.mycompany.entity.Offer;

import java.util.List;
import java.util.Set;

public interface TransactionService {
    List<Deal> transactionProcess(Set<Offer> offers, Offer offer);
}
