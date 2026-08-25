package com.expensemanager.contoller;

import java.time.format.DateTimeFormatter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.transaction.annotation.Transactional;
import com.expensemanager.dto.ChitRequest;
import com.expensemanager.dto.MemberRequest;
import com.expensemanager.dto.MemberResponse;
import com.expensemanager.dto.InstallmentRequest;
import com.expensemanager.dto.InstallmentResponse;
import com.expensemanager.dto.AuctionRequest;
import com.expensemanager.dto.AuctionResponse;
import com.expensemanager.dto.ChitDashboardResponse;
import com.expensemanager.entity.Chit;
import com.expensemanager.entity.ChitStatus;
import com.expensemanager.entity.Member;
import com.expensemanager.entity.Installment;
import com.expensemanager.entity.Auction;
import com.expensemanager.repository.ChitRepository;
import com.expensemanager.repository.MemberRepository;
import com.expensemanager.repository.InstallmentRepository;
import com.expensemanager.repository.AuctionRepository;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ChitController {
    private static final DateTimeFormatter ID_DATE = DateTimeFormatter.ofPattern("yyyy-MM");
    private static final String EXTRA_HAND_YES = "yes";
    private static final String AVAILABLE_BALANCE_MESSAGE = "Available balance is less. We can not do bid.";
    private final ChitRepository chitRepository;
    private final MemberRepository memberRepository;
    private final InstallmentRepository installmentRepository;
    private final AuctionRepository auctionRepository;

    public ChitController(ChitRepository chitRepository, MemberRepository memberRepository,
            InstallmentRepository installmentRepository, AuctionRepository auctionRepository) {
        this.chitRepository = chitRepository;
        this.memberRepository = memberRepository;
        this.installmentRepository = installmentRepository;
        this.auctionRepository = auctionRepository;
    }

    @GetMapping("/chits")
    public List<Chit> chits() {
        return chitRepository.findAllByOrderByStartDateDesc();
    }

    @GetMapping("/chits/{id}/summary")
    public ResponseEntity<ChitDashboardResponse> chitSummary(@PathVariable java.util.UUID id) {
        Chit chit = chitRepository.findById(id).orElse(null);
        if (chit == null)
            return ResponseEntity.notFound().build();
        Integer latestHand = installmentRepository.findMaxHandByChitId(id);
        BigDecimal collection = latestHand == null ? BigDecimal.ZERO
                : installmentRepository.sumAmountByChitIdAndHand(id, latestHand);
        long paidMembers = latestHand == null ? 0 : installmentRepository.countByChitIdAndHand(id, latestHand);
        long membersNotPaid = Math.max(0, chit.getMemberCount() - paidMembers);
        return ResponseEntity.ok(new ChitDashboardResponse(id, collection,
                auctionRepository.maxProfitAmountByChitId(id), membersNotPaid, latestHand,
                auctionRepository.countRegularHandsByChitId(id), auctionRepository.countExtraHandsByChitId(id),
                auctionRepository.sumNetAmountPaidByChitId(id), auctionRepository.sumAgentAmountByChitId(id)));
    }

    @PostMapping("/chits")
    public ResponseEntity<Chit> createChit(@Valid @RequestBody ChitRequest request) {
        long sequence = chitRepository.count() + 1;
        String chitId = "%s-%s-%d-%03d".formatted(request.startDate().format(ID_DATE),
                request.totalAmount().stripTrailingZeros().toPlainString(), request.memberCount(), sequence);
        Chit chit = new Chit(chitId, request.name(), request.totalAmount(), request.memberCount(),
                request.monthlyInstallment(), request.agentPercentage(), request.startDate(), request.durationMonths());
        return ResponseEntity.status(HttpStatus.CREATED).body(chitRepository.save(chit));
    }

    @PutMapping("/chits/{id}")
    public ResponseEntity<Chit> updateChit(@PathVariable java.util.UUID id, @Valid @RequestBody ChitRequest request) {
        return chitRepository.findById(id).map(chit -> {
            chit.update(request.name(), request.totalAmount(), request.memberCount(), request.monthlyInstallment(),
                    request.agentPercentage(), request.startDate(), request.durationMonths());
            return ResponseEntity.ok(chitRepository.save(chit));
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/chits/{id}")
    @Transactional
    public ResponseEntity<Void> deleteChit(@PathVariable java.util.UUID id) {
        if (!chitRepository.existsById(id))
            return ResponseEntity.notFound().build();
        installmentRepository.deleteByChitId(id);
        auctionRepository.deleteByChitId(id);
        memberRepository.deleteByChitId(id);
        chitRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/members")
    public List<MemberResponse> members(@RequestParam(required = false) java.util.UUID chitId) {
        List<Member> members = chitId == null ? memberRepository.findAllByOrderByNameAsc()
                : memberRepository.findAllByChitIdOrderByNameAsc(chitId);
        return members.stream().map(MemberResponse::from).toList();
    }

    @PostMapping("/members")
    public ResponseEntity<MemberResponse> createMember(@Valid @RequestBody MemberRequest request) {
        Chit chit = chitRepository.findById(request.chitId()).orElse(null);
        if (chit == null)
            return ResponseEntity.notFound().build();
        Member member = new Member(chit, request.name(), request.mobileNumber(), request.email(),
                request.permanentAddress());
        member.update(chit, request.name(), request.mobileNumber(), request.email(), request.permanentAddress());
        return ResponseEntity.status(HttpStatus.CREATED).body(MemberResponse.from(memberRepository.save(member)));
    }

    @PutMapping("/members/{id}")
    public ResponseEntity<MemberResponse> updateMember(@PathVariable java.util.UUID id,
            @Valid @RequestBody MemberRequest request) {
        return memberRepository.findById(id).map(member -> chitRepository.findById(request.chitId()).map(chit -> {
            member.update(chit, request.name(), request.mobileNumber(), request.email(), request.permanentAddress());
            return ResponseEntity.ok(MemberResponse.from(memberRepository.save(member)));
        }).orElseGet(() -> ResponseEntity.notFound().build())).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/members/{id}")
    @Transactional
    public ResponseEntity<Void> deleteMember(@PathVariable java.util.UUID id) {
        if (!memberRepository.existsById(id))
            return ResponseEntity.notFound().build();
        installmentRepository.deleteByMemberId(id);
        memberRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/installments")
    public List<InstallmentResponse> installments(@RequestParam(required = false) java.util.UUID chitId,
            @RequestParam(required = false) java.util.UUID memberId) {
        List<Installment> installments;
        if (memberId != null)
            installments = installmentRepository.findAllByMemberIdOrderByInstallmentDateDesc(memberId);
        else if (chitId != null)
            installments = installmentRepository.findAllByChitIdOrderByInstallmentDateDesc(chitId);
        else
            installments = installmentRepository.findAllByOrderByInstallmentDateDesc();
        return installments.stream().map(InstallmentResponse::from).toList();
    }

    @PostMapping("/installments")
    public ResponseEntity<InstallmentResponse> createInstallment(@Valid @RequestBody InstallmentRequest request) {
        Chit chit = chitRepository.findById(request.chitId()).orElse(null);
        Member member = memberRepository.findById(request.memberId()).orElse(null);
        if (chit == null || member == null || !member.getChit().getId().equals(chit.getId())) {
            return ResponseEntity.notFound().build();
        }
        Installment installment = new Installment(chit, member, request.numberOfHand(), request.installmentAmount(),
                request.installmentDate());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(InstallmentResponse.from(installmentRepository.save(installment)));
    }

    @PutMapping("/installments/{id}")
    public ResponseEntity<InstallmentResponse> updateInstallment(@PathVariable java.util.UUID id,
            @Valid @RequestBody InstallmentRequest request) {
        return installmentRepository.findById(id).map(installment -> {
            Chit chit = chitRepository.findById(request.chitId()).orElse(null);
            Member member = memberRepository.findById(request.memberId()).orElse(null);
            if (chit == null || member == null || !member.getChit().getId().equals(chit.getId())) {
                return ResponseEntity.notFound().<InstallmentResponse>build();
            }
            installment.update(chit, member, request.numberOfHand(), request.installmentAmount(),
                    request.installmentDate());
            return ResponseEntity.ok(InstallmentResponse.from(installmentRepository.save(installment)));
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/auctions")
    public List<AuctionResponse> auctions(@RequestParam(required = false) java.util.UUID chitId) {
        List<Auction> auctions = chitId == null ? auctionRepository.findAllByOrderByAuctionMonthDescBidNoDesc()
                : auctionRepository.findAllByChitIdOrderByAuctionMonthDescBidNoDesc(chitId);
        return auctions.stream().map(AuctionResponse::from).toList();
    }

    @PostMapping("/auctions")
    @Transactional
    public ResponseEntity<?> createAuction(@Valid @RequestBody AuctionRequest request) {
        return saveAuction(null, request);
    }

    @PutMapping("/auctions/{id}")
    @Transactional
    public ResponseEntity<?> updateAuction(@PathVariable java.util.UUID id,
            @Valid @RequestBody AuctionRequest request) {
        Auction auction = auctionRepository.findById(id).orElse(null);
        if (auction == null)
            return ResponseEntity.notFound().build();
        return saveAuction(auction, request);
    }

    @DeleteMapping("/auctions/{id}")
    @Transactional
    public ResponseEntity<Void> deleteAuction(@PathVariable java.util.UUID id) {
        Auction auction = auctionRepository.findById(id).orElse(null);
        if (auction == null)
            return ResponseEntity.notFound().build();
        auction.getWinningMember().markChitTaken(false);
        memberRepository.save(auction.getWinningMember());
        auctionRepository.delete(auction);
        return ResponseEntity.noContent().build();
    }

    private ResponseEntity<?> saveAuction(Auction existing, AuctionRequest request) {
        Chit chit = chitRepository.findById(request.chitId()).orElse(null);
        Member member = memberRepository.findById(request.winningMemberId()).orElse(null);
        if (chit == null || member == null || !member.getChit().getId().equals(chit.getId())
                || request.bidAmount().compareTo(chit.getTotalAmount()) > 0) {
            return ResponseEntity.notFound().build();
        }
        boolean extraHandSelected = request.extraHand();
        BigDecimal agentAmount = chit.getAgentPercentage().compareTo(BigDecimal.ZERO) > 0
                ? chit.getMonthlyInstallment().multiply(chit.getAgentPercentage()).divide(BigDecimal.valueOf(100), 2,
                        RoundingMode.HALF_UP)
                : BigDecimal.ZERO;
        BigDecimal netAmountPaid = chit.getTotalAmount().subtract(request.bidAmount());
        BigDecimal profitAmount = request.bidAmount().subtract(agentAmount);
        if (profitAmount.signum() < 0) {
            return ResponseEntity.badRequest().body(Map.of("message", "Available balance is less. We can not do bid."));
        }
        if (extraHandSelected) {
            BigDecimal previousProfitTotal = auctionRepository.sumProfitAmountByChitId(chit.getId())
                    .orElse(BigDecimal.ZERO);
            if (existing != null) {
                previousProfitTotal = previousProfitTotal.subtract(existing.getProfitAmount());
            }
            BigDecimal requiredBalance = profitAmount.add(previousProfitTotal);
            if (netAmountPaid.compareTo(requiredBalance) < 0) {
                return ResponseEntity.badRequest().body(Map.of("message", AVAILABLE_BALANCE_MESSAGE));
            }

            List<Auction> chitAuctions = auctionRepository
                    .findAllByChitIdOrderByAuctionMonthDescBidNoDesc(chit.getId());
            for (Auction chitAuction : chitAuctions) {
                if (existing == null || !chitAuction.getId().equals(existing.getId())) {
                    chitAuction.setProfitAmount(BigDecimal.ZERO);
                    auctionRepository.save(chitAuction);
                }
            }
            profitAmount = requiredBalance.subtract(netAmountPaid);
        }
        if (existing != null && !existing.getWinningMember().getId().equals(member.getId())) {
            existing.getWinningMember().markChitTaken(false);
        }
        boolean extraHandValue = request.extraHand();
        Auction auction = existing == null
                ? new Auction(chit, request.bidNo(), extraHandValue, request.auctionMonth(), request.bidAmount(),
                        member,
                        netAmountPaid, agentAmount, profitAmount)
                : existing;
        if (existing != null)
            auction.update(chit, request.bidNo(), extraHandValue, request.auctionMonth(), request.bidAmount(),
                    member,
                    netAmountPaid, agentAmount, profitAmount);
        member.markChitTaken(true);
        return ResponseEntity.status(existing == null ? HttpStatus.CREATED : HttpStatus.OK)
                .body(AuctionResponse.from(auctionRepository.save(auction)));
    }

    @DeleteMapping("/installments/{id}")
    public ResponseEntity<Void> deleteInstallment(@PathVariable java.util.UUID id) {
        if (!installmentRepository.existsById(id))
            return ResponseEntity.notFound().build();
        installmentRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/chit-summary")
    public Map<String, Object> summary() {
        return Map.of("totalChits", chitRepository.count(), "activeChits",
                chitRepository.countByStatus(ChitStatus.active),
                "totalMembers", memberRepository.count());
    }
}
