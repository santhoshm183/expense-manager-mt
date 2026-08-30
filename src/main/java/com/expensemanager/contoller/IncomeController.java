package com.expensemanager.contoller;

import java.util.List;
import java.util.UUID;
import java.math.BigDecimal;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.expensemanager.dto.IncomeRequest;
import com.expensemanager.dto.IncomeResponse;
import com.expensemanager.service.IncomeService;

@RestController
@RequestMapping("/api/incomes")
@CrossOrigin(origins = "*")
public class IncomeController {
    private final IncomeService incomeService;

    public IncomeController(IncomeService incomeService) {
        this.incomeService = incomeService;
    }

    @GetMapping
    public List<IncomeResponse> incomes(@RequestParam UUID chitId) {
        return incomeService.findAll(chitId);
    }

    @GetMapping("/amount")
    public BigDecimal currentAmount(@RequestParam UUID chitId) {
        return incomeService.currentAmount(chitId);
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody IncomeRequest request) {
        return incomeService.create(request);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @Valid @RequestBody IncomeRequest request) {
        return incomeService.update(id, request);
    }
}
