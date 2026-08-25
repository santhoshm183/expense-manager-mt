package com.expensemanager.repository;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import com.expensemanager.entity.Auction;

public interface AuctionRepository extends JpaRepository<Auction, UUID> {
    @EntityGraph(attributePaths = { "chit", "winningMember" })
    List<Auction> findAllByOrderByAuctionMonthDescBidNoDesc();

    @EntityGraph(attributePaths = { "chit", "winningMember" })
    List<Auction> findAllByChitIdOrderByAuctionMonthDescBidNoDesc(UUID chitId);

    long deleteByChitId(UUID chitId);
}
