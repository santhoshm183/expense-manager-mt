package com.expensemanager.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.expensemanager.dto.ChitDashboardResponse;
import com.expensemanager.dto.ChitRequest;
import com.expensemanager.entity.Chit;
import com.expensemanager.entity.ChitStatus;
import com.expensemanager.repository.AuctionRepository;
import com.expensemanager.repository.ChitRepository;
import com.expensemanager.repository.InstallmentRepository;
import com.expensemanager.repository.MemberRepository;

@Service
public class ChitService {
    private static final DateTimeFormatter ID_DATE = DateTimeFormatter.ofPattern("yyyy-MM");
    private final ChitRepository chitRepository;
    private final MemberRepository memberRepository;
    private final InstallmentRepository installmentRepository;
    private final AuctionRepository auctionRepository;

    public ChitService(ChitRepository chitRepository, MemberRepository memberRepository,
            InstallmentRepository installmentRepository, AuctionRepository auctionRepository) {
        this.chitRepository = chitRepository;
        this.memberRepository = memberRepository;
        this.installmentRepository = installmentRepository;
        this.auctionRepository = auctionRepository;
    }

    public List<Chit> findAll() {
        return chitRepository.findAllByOrderByStartDateDesc();
    }

    @Transactional(readOnly = true)
    public ResponseEntity<ChitDashboardResponse> summary(UUID id) {
        Chit chit = chitRepository.findById(id).orElse(null);
        if (chit == null)
            return ResponseEntity.notFound().build();
        LocalDate latestAuction = auctionRepository.findLatestAuctionMonthByChitId(id);
        if (latestAuction == null) {
            return ResponseEntity.ok(new ChitDashboardResponse(id, BigDecimal.ZERO, BigDecimal.ZERO,
                    chit.getMemberCount(), 0, 0, 0, BigDecimal.ZERO, BigDecimal.ZERO));
        }
        Integer latestHand = installmentRepository.findMaxHandAfterAuction(id, latestAuction);
        BigDecimal collection = installmentRepository.sumAmountAfterAuction(id, latestAuction);
        long membersNotPaid = chit.getMemberCount();
        if (collection != null && collection.compareTo(BigDecimal.ZERO) < 0) {
            long paidMembers = installmentRepository.countAfterAuction(id, latestAuction);
            membersNotPaid = Math.max(0, chit.getMemberCount() - paidMembers);
        }

        return ResponseEntity.ok(new ChitDashboardResponse(id, collection,
                auctionRepository.maxProfitAmountByChitId(id), membersNotPaid, latestHand,
                auctionRepository.countRegularHandsByChitId(id), auctionRepository.countExtraHandsByChitId(id),
                auctionRepository.sumNetAmountPaidByChitId(id), auctionRepository.sumAgentAmountByChitId(id)));
    }

    @Transactional
    public Chit create(ChitRequest request) {
        long sequence = chitRepository.count() + 1;
        String chitId = "%s-%s-%d-%03d".formatted(request.startDate().format(ID_DATE),
                request.totalAmount().stripTrailingZeros().toPlainString(), request.memberCount(), sequence);
        Chit chit = new Chit(chitId, request.name(), request.totalAmount(), request.memberCount(),
                request.monthlyInstallment(), request.agentPercentage(), request.startDate(), request.durationMonths());
        return chitRepository.save(chit);
    }

    @Transactional
    public ResponseEntity<Chit> update(UUID id, ChitRequest request) {
        return chitRepository.findById(id).map(chit -> {
            chit.update(request.name(), request.totalAmount(), request.memberCount(), request.monthlyInstallment(),
                    request.agentPercentage(), request.startDate(), request.durationMonths());
            return ResponseEntity.ok(chitRepository.save(chit));
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Transactional
    public ResponseEntity<Void> delete(UUID id) {
        if (!chitRepository.existsById(id))
            return ResponseEntity.notFound().build();
        installmentRepository.deleteByChitId(id);
        auctionRepository.deleteByChitId(id);
        memberRepository.deleteByChitId(id);
        chitRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    public Map<String, Object> totals() {
        return Map.of("totalChits", chitRepository.count(), "activeChits",
                chitRepository.countByStatus(ChitStatus.active), "totalMembers", memberRepository.count());
    }
}
