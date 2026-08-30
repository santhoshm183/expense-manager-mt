package com.expensemanager.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.math.BigDecimal;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.expensemanager.entity.Income;

public interface IncomeRepository extends JpaRepository<Income, UUID> {
    @EntityGraph(attributePaths = "chit")
    List<Income> findAllByChitIdOrderByCreatedAtDesc(UUID chitId);

    Optional<Income> findFirstByChitIdAndActiveTrue(UUID chitId);

    @Query("select coalesce(sum(i.interestEarnedAmount), 0) from Income i where i.chit.id = :chitId and i.active = true")
    BigDecimal sumActiveInterestEarnedAmountByChitId(@Param("chitId") UUID chitId);
}
