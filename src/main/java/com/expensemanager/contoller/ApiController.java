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
import com.expensemanager.service.TransactionService;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ApiController {
    private final TransactionService transactionService;

    public ApiController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of("status", "ok", "service", "expense-manager-mt");
    }

    @GetMapping("/transactions")
    public List<Transaction> transactions() {
        return transactionService.findAll();
    }

    @PostMapping("/transactions")
    public ResponseEntity<Transaction> create(@Valid @RequestBody TransactionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(transactionService.create(request));
    }

    @PutMapping("/transactions/{id}")
    public ResponseEntity<Transaction> update(@PathVariable UUID id, @Valid @RequestBody TransactionRequest request) {
        return transactionService.update(id, request);
    }

    @DeleteMapping("/transactions/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        return transactionService.delete(id);
    }

}
