package com.expensemanager.repository;

import java.util.List;
import java.util.UUID;
import java.time.LocalDate;
import java.math.BigDecimal;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import com.expensemanager.entity.Installment;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface InstallmentRepository extends JpaRepository<Installment, UUID> {
    @EntityGraph(attributePaths = { "chit", "member" })
    List<Installment> findAllByOrderByInstallmentDateDesc();

    @EntityGraph(attributePaths = { "chit", "member" })
    List<Installment> findAllByChitIdOrderByInstallmentDateDesc(UUID chitId);

    @EntityGraph(attributePaths = { "chit", "member" })
    List<Installment> findAllByMemberIdOrderByInstallmentDateDesc(UUID memberId);

    long deleteByChitId(UUID chitId);

    long deleteByMemberId(UUID memberId);

    @Query("select max(i.numberOfHand) from Installment i where i.chit.id = :chitId")
    Integer findMaxHandByChitId(@Param("chitId") UUID chitId);

    @Query("select coalesce(sum(i.installmentAmount), 0) from Installment i where i.chit.id = :chitId and i.numberOfHand = :hand")
    BigDecimal sumAmountByChitIdAndHand(@Param("chitId") UUID chitId, @Param("hand") Integer hand);

    @Query("select count(i) from Installment i where i.chit.id = :chitId and i.numberOfHand = :hand")
    long countByChitIdAndHand(@Param("chitId") UUID chitId, @Param("hand") Integer hand);

    @Query("select coalesce(sum(i.installmentAmount), 0) from Installment i where i.chit.id = :chitId and i.installmentDate > :after")
    BigDecimal sumAmountAfterAuction(@Param("chitId") UUID chitId, @Param("after") LocalDate after);

    @Query("select coalesce(sum(i.installmentAmount), 0) from Installment i where i.chit.id = :chitId")
    BigDecimal sumAmount(@Param("chitId") UUID chitId);

    @Query("select count(i) from Installment i where i.chit.id = :chitId and i.installmentDate > :after")
    long countAfterAuction(@Param("chitId") UUID chitId, @Param("after") LocalDate after);

    @Query("select max(i.numberOfHand) from Installment i where i.chit.id = :chitId and i.installmentDate > :after")
    Integer findMaxHandAfterAuction(@Param("chitId") UUID chitId, @Param("after") LocalDate after);

    @Query("select coalesce(max(i.numberOfHand), 0) from Installment i where i.member.id = :memberId")
    Integer findMaxHandByMemberId(@Param("memberId") UUID memberId);

    @Query("select coalesce(max(i.numberOfHand), 0) from Installment i where i.chit.id = :chitId")
    Integer findMaxHandByChitIdOrZero(@Param("chitId") UUID chitId);

    @EntityGraph(attributePaths = { "chit", "member" })
    @Query("select i from Installment i where (:chitId is null or i.chit.id = :chitId) and (:memberId is null or i.member.id = :memberId) and (:hand is null or i.numberOfHand = :hand) order by i.installmentDate desc")
    List<Installment> findFiltered(@Param("chitId") UUID chitId, @Param("memberId") UUID memberId,
            @Param("hand") Integer hand);
}
