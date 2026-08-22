package com.expensemanager.contoller;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.expensemanager.dto.TransactionRequest;
import com.expensemanager.entity.Transaction;
import com.expensemanager.repository.TransactionRepository;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ApiController {
    private final TransactionRepository transactionRepository;

    public ApiController(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of("status", "ok", "service", "expense-manager-mt");
    }

    @GetMapping("/transactions")
    public List<Transaction> transactions() {
        return transactionRepository.findAllByOrderByTransactionDateDesc();
    }

    @PostMapping("/transactions")
    public ResponseEntity<Transaction> create(@Valid @RequestBody TransactionRequest request) {
        Transaction transaction = new Transaction(UUID.randomUUID(), request.date(), request.description(),
                request.amount(), request.type());
        return ResponseEntity.status(HttpStatus.CREATED).body(transactionRepository.save(transaction));
    }

    @PutMapping("/transactions/{id}")
    public ResponseEntity<Transaction> update(@PathVariable UUID id, @Valid @RequestBody TransactionRequest request) {
        return transactionRepository.findById(id).map(transaction -> {
            transaction.update(request.date(), request.description(), request.amount(), request.type());
            return ResponseEntity.ok(transactionRepository.save(transaction));
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/transactions/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        if (!transactionRepository.existsById(id))
            return ResponseEntity.notFound().build();
        transactionRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

}
