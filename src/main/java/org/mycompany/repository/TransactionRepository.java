package org.mycompany.repository;

import org.mycompany.entity.Transaction;
import org.springframework.data.repository.Repository;

public interface TransactionRepository extends Repository<Transaction, Long> {
    Transaction save(Transaction offer);
}
