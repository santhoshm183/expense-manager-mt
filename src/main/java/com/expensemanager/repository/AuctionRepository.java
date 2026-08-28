package com.expensemanager.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.expensemanager.entity.Auction;

public interface AuctionRepository extends JpaRepository<Auction, UUID> {
    @EntityGraph(attributePaths = { "chit", "winningMember" })
    List<Auction> findAllByOrderByAuctionMonthDescBidNoDesc();

    @EntityGraph(attributePaths = { "chit", "winningMember" })
    List<Auction> findAllByChitIdOrderByAuctionMonthDescBidNoDesc(UUID chitId);

    @Query("select coalesce(sum(a.profitAmount), 0) from Auction a where a.chit.id = :chitId")
    Optional<BigDecimal> sumProfitAmountByChitId(@Param("chitId") UUID chitId);

    @Query("select count(a) from Auction a where a.chit.id = :chitId and a.handType <> 'ExtrHand'")
    long countRegularHandsByChitId(@Param("chitId") UUID chitId);

    @Query("select count(a) from Auction a where a.chit.id = :chitId and a.handType = 'ExtrHand'")
    long countExtraHandsByChitId(@Param("chitId") UUID chitId);

    @Query("select coalesce(sum(a.netAmountPaid), 0) from Auction a where a.chit.id = :chitId")
    BigDecimal sumNetAmountPaidByChitId(@Param("chitId") UUID chitId);

    @Query("select coalesce(sum(a.agentAmount), 0) from Auction a where a.chit.id = :chitId")
    BigDecimal sumAgentAmountByChitId(@Param("chitId") UUID chitId);

    @Query("select coalesce(sum(a.profitAmount), 0) from Auction a where a.chit.id = :chitId")
    BigDecimal maxProfitAmountByChitId(@Param("chitId") UUID chitId);

    @Query("select max(a.auctionMonth) from Auction a where a.chit.id = :chitId")
    LocalDate findLatestAuctionMonthByChitId(@Param("chitId") UUID chitId);

    @Query("select coalesce(max(a.bidNo), 0) from Auction a where a.chit.id = :chitId")
    Integer findMaxBidNoByChitId(@Param("chitId") UUID chitId);

    long deleteByChitId(UUID chitId);
}
