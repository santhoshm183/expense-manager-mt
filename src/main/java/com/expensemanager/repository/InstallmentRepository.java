package com.expensemanager.repository;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import com.expensemanager.entity.Installment;

public interface InstallmentRepository extends JpaRepository<Installment, UUID> {
    @EntityGraph(attributePaths = { "chit", "member" })
    List<Installment> findAllByOrderByInstallmentDateDesc();

    @EntityGraph(attributePaths = { "chit", "member" })
    List<Installment> findAllByChitIdOrderByInstallmentDateDesc(UUID chitId);

    @EntityGraph(attributePaths = { "chit", "member" })
    List<Installment> findAllByMemberIdOrderByInstallmentDateDesc(UUID memberId);

    long deleteByChitId(UUID chitId);

    long deleteByMemberId(UUID memberId);
}
