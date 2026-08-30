package com.expensemanager.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.expensemanager.dto.IncomeRequest;
import com.expensemanager.dto.IncomeResponse;
import com.expensemanager.entity.Chit;
import com.expensemanager.entity.Income;
import com.expensemanager.repository.AuctionRepository;
import com.expensemanager.repository.ChitRepository;
import com.expensemanager.repository.IncomeRepository;

@Service
public class IncomeService {
    private final ChitRepository chitRepository;
    private final AuctionRepository auctionRepository;
    private final IncomeRepository incomeRepository;

    public IncomeService(ChitRepository chitRepository, AuctionRepository auctionRepository,
            IncomeRepository incomeRepository) {
        this.chitRepository = chitRepository;
        this.auctionRepository = auctionRepository;
        this.incomeRepository = incomeRepository;
    }

    public List<IncomeResponse> findAll(UUID chitId) {
        return incomeRepository.findAllByChitIdOrderByCreatedAtDesc(chitId).stream()
                .map(IncomeResponse::from).toList();
    }

    public BigDecimal currentAmount(UUID chitId) {
        return chitRepository.findById(chitId).map(this::incomeAmount).orElse(BigDecimal.ZERO);
    }

    @Transactional
    public ResponseEntity<?> create(IncomeRequest request) {
        Chit chit = chitRepository.findById(request.chitId()).orElse(null);
        if (chit == null)
            return ResponseEntity.notFound().build();
        incomeRepository.findFirstByChitIdAndActiveTrue(chit.getId()).ifPresent(previous -> {
            previous.deactivate();
            incomeRepository.save(previous);
        });
        return ResponseEntity.status(HttpStatus.CREATED).body(IncomeResponse.from(save(chit, request)));
    }

    @Transactional
    public ResponseEntity<?> update(UUID id, IncomeRequest request) {
        Income income = incomeRepository.findById(id).orElse(null);
        Chit chit = chitRepository.findById(request.chitId()).orElse(null);
        if (income == null || chit == null || !income.getChit().getId().equals(chit.getId()))
            return ResponseEntity.notFound().build();
        if (!income.isActive())
            return ResponseEntity.badRequest().body(Map.of("message", "Only the active income can be edited."));
        return ResponseEntity.ok(IncomeResponse.from(save(chit, request, income)));
    }

    private Income save(Chit chit, IncomeRequest request) {
        return save(chit, request, new Income(chit, incomeAmount(chit), request.percentage(),
                request.numberOfMonths(), interest(chit, request)));
    }

    private Income save(Chit chit, IncomeRequest request, Income income) {
        income.update(incomeAmount(chit), request.percentage(), request.numberOfMonths(), interest(chit, request));
        return incomeRepository.save(income);
    }

    private BigDecimal incomeAmount(Chit chit) {
        return auctionRepository.sumProfitAmountByChitId(chit.getId()).orElse(BigDecimal.ZERO);
    }

    private BigDecimal interest(Chit chit, IncomeRequest request) {
        return incomeAmount(chit).multiply(request.percentage()).multiply(BigDecimal.valueOf(request.numberOfMonths()))
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }
}
