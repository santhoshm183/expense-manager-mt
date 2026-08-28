package com.expensemanager.service;

import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.expensemanager.dto.TransactionRequest;
import com.expensemanager.entity.Transaction;
import com.expensemanager.repository.TransactionRepository;

@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public List<Transaction> findAll() {
        return transactionRepository.findAllByOrderByTransactionDateDesc();
    }

    @Transactional
    public Transaction create(TransactionRequest request) {
        Transaction transaction = new Transaction(UUID.randomUUID(), request.date(), request.description(),
                request.amount(), request.type());
        return transactionRepository.save(transaction);
    }

    @Transactional
    public ResponseEntity<Transaction> update(UUID id, TransactionRequest request) {
        return transactionRepository.findById(id).map(transaction -> {
            transaction.update(request.date(), request.description(), request.amount(), request.type());
            return ResponseEntity.ok(transactionRepository.save(transaction));
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Transactional
    public ResponseEntity<Void> delete(UUID id) {
        if (!transactionRepository.existsById(id))
            return ResponseEntity.notFound().build();
        transactionRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
