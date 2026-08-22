package com.expensemanager.repository;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

import com.expensemanager.entity.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
    List<Transaction> findAllByOrderByTransactionDateDesc();
}