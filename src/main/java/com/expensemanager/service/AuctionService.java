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
import com.expensemanager.dto.AuctionRequest;
import com.expensemanager.dto.AuctionResponse;
import com.expensemanager.entity.Auction;
import com.expensemanager.entity.Chit;
import com.expensemanager.entity.Member;
import com.expensemanager.repository.AuctionRepository;
import com.expensemanager.repository.ChitRepository;
import com.expensemanager.repository.MemberRepository;

@Service
public class AuctionService {
    private static final String AVAILABLE_BALANCE_MESSAGE = "Available balance is less. We can not do bid.";
    private final ChitRepository chitRepository;
    private final MemberRepository memberRepository;
    private final AuctionRepository auctionRepository;

    public AuctionService(ChitRepository chitRepository, MemberRepository memberRepository,
            AuctionRepository auctionRepository) {
        this.chitRepository = chitRepository;
        this.memberRepository = memberRepository;
        this.auctionRepository = auctionRepository;
    }

    public List<AuctionResponse> findAll(UUID chitId) {
        List<Auction> auctions = chitId == null ? auctionRepository.findAllByOrderByAuctionMonthDescBidNoDesc()
                : auctionRepository.findAllByChitIdOrderByAuctionMonthDescBidNoDesc(chitId);
        return auctions.stream().map(AuctionResponse::from).toList();
    }

    @Transactional
    public ResponseEntity<?> create(AuctionRequest request) {
        return save(null, request);
    }

    @Transactional
    public ResponseEntity<?> update(UUID id, AuctionRequest request) {
        Auction auction = auctionRepository.findById(id).orElse(null);
        if (auction == null)
            return ResponseEntity.notFound().build();
        return save(auction, request);
    }

    @Transactional
    public ResponseEntity<Void> delete(UUID id) {
        Auction auction = auctionRepository.findById(id).orElse(null);
        if (auction == null)
            return ResponseEntity.notFound().build();
        auction.getWinningMember().markChitTaken(false);
        memberRepository.save(auction.getWinningMember());
        auctionRepository.delete(auction);
        return ResponseEntity.noContent().build();
    }

    private ResponseEntity<?> save(Auction existing, AuctionRequest request) {
        Chit chit = chitRepository.findById(request.chitId()).orElse(null);
        Member member = memberRepository.findById(request.winningMemberId()).orElse(null);
        if (!belongsToChit(chit, member) || request.bidAmount().compareTo(chit.getTotalAmount()) > 0)
            return ResponseEntity.notFound().build();

        String handType = request.handType().trim();
        if (!List.of("ExtrHand", "ReleaseHand", "AgentHand").contains(handType))
            return ResponseEntity.badRequest().body(Map.of("message", "Invalid hand type."));

        BigDecimal agentPercentage = "AgentHand".equals(handType) ? BigDecimal.ZERO : chit.getAgentPercentage();
        BigDecimal agentAmount = agentPercentage.compareTo(BigDecimal.ZERO) > 0
                ? chit.getTotalAmount().multiply(agentPercentage).divide(BigDecimal.valueOf(100), 2,
                        RoundingMode.HALF_UP)
                : BigDecimal.ZERO;
        BigDecimal netAmountPaid = chit.getTotalAmount().subtract(request.bidAmount());
        BigDecimal profitAmount = request.bidAmount().subtract(agentAmount);
        BigDecimal profitValue = profitAmount;
        if (profitAmount.signum() < 0)
            return ResponseEntity.badRequest().body(Map.of("message", AVAILABLE_BALANCE_MESSAGE));

        if ("ExtrHand".equals(handType)) {

            BigDecimal previousProfitTotal = auctionRepository.sumProfitAmountByChitId(chit.getId())
                    .orElse(BigDecimal.ZERO);
            if (existing != null)
                previousProfitTotal = previousProfitTotal.subtract(existing.getProfitAmount());

            BigDecimal requiredBalance = previousProfitTotal;
            if (!request.partialAmount()) {
                if (requiredBalance.compareTo(netAmountPaid) < 0)
                    return ResponseEntity.badRequest().body(Map.of("message", AVAILABLE_BALANCE_MESSAGE));
            }
            profitAmount = requiredBalance.subtract(netAmountPaid);
            for (Auction auction : auctionRepository.findAllByChitIdOrderByAuctionMonthDescBidNoDesc(chit.getId())) {
                if (existing == null || !auction.getId().equals(existing.getId())) {
                    auction.setProfitAmount(BigDecimal.ZERO);
                    auctionRepository.save(auction);
                }
            }
        }

        if (existing != null && !existing.getWinningMember().getId().equals(member.getId()))
            existing.getWinningMember().markChitTaken(false);

        Integer bidNo = request.bidNo() == null ? auctionRepository.findMaxBidNoByChitId(chit.getId()) + 1
                : request.bidNo();
        Auction auction = existing == null
                ? new Auction(chit, bidNo, handType, request.partialAmount(), request.auctionMonth(),
                        request.bidAmount(),
                        member, netAmountPaid, agentAmount, profitAmount, profitValue)
                : existing;
        if (existing != null)
            auction.update(chit, bidNo, handType, request.partialAmount(), request.auctionMonth(), request.bidAmount(),
                    member, netAmountPaid, agentAmount, profitAmount, profitValue);
        member.markChitTaken(true);
        return ResponseEntity.status(existing == null ? HttpStatus.CREATED : HttpStatus.OK)
                .body(AuctionResponse.from(auctionRepository.save(auction)));
    }

    private boolean belongsToChit(Chit chit, Member member) {
        return chit != null && member != null && member.getChit().getId().equals(chit.getId());
    }
}
