package com.expensemanager.service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.expensemanager.dto.InstallmentRequest;
import com.expensemanager.dto.InstallmentResponse;
import com.expensemanager.entity.Chit;
import com.expensemanager.entity.Installment;
import com.expensemanager.entity.Member;
import com.expensemanager.repository.ChitRepository;
import com.expensemanager.repository.InstallmentRepository;
import com.expensemanager.repository.MemberRepository;

@Service
public class InstallmentService {
    private final ChitRepository chitRepository;
    private final MemberRepository memberRepository;
    private final InstallmentRepository installmentRepository;

    public InstallmentService(ChitRepository chitRepository, MemberRepository memberRepository,
            InstallmentRepository installmentRepository) {
        this.chitRepository = chitRepository;
        this.memberRepository = memberRepository;
        this.installmentRepository = installmentRepository;
    }

    public List<InstallmentResponse> findAll(UUID chitId, UUID memberId) {
        List<Installment> installments;
        if (memberId != null)
            installments = installmentRepository.findAllByMemberIdOrderByInstallmentDateDesc(memberId);
        else if (chitId != null)
            installments = installmentRepository.findAllByChitIdOrderByInstallmentDateDesc(chitId);
        else
            installments = installmentRepository.findAllByOrderByInstallmentDateDesc();
        return installments.stream().map(InstallmentResponse::from).toList();
    }

    @Transactional
    public ResponseEntity<?> create(InstallmentRequest request) {
        Chit chit = chitRepository.findById(request.chitId()).orElse(null);
        Member member = memberRepository.findById(request.memberId()).orElse(null);
        if (!belongsToChit(chit, member))
            return ResponseEntity.notFound().build();
        if (request.numberOfHand() > chit.getDurationMonths())
            return invalidHand();
        Installment installment = new Installment(chit, member, request.numberOfHand(), request.installmentAmount(),
                request.installmentDate());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(InstallmentResponse.from(installmentRepository.save(installment)));
    }

    @Transactional
    public ResponseEntity<?> update(UUID id, InstallmentRequest request) {
        Installment installment = installmentRepository.findById(id).orElse(null);
        if (installment == null)
            return ResponseEntity.notFound().build();
        Chit chit = chitRepository.findById(request.chitId()).orElse(null);
        Member member = memberRepository.findById(request.memberId()).orElse(null);
        if (!belongsToChit(chit, member))
            return ResponseEntity.notFound().build();
        if (request.numberOfHand() > chit.getDurationMonths())
            return invalidHand();
        installment.update(chit, member, request.numberOfHand(), request.installmentAmount(),
                request.installmentDate());
        return ResponseEntity.ok(InstallmentResponse.from(installmentRepository.save(installment)));
    }

    public ResponseEntity<Void> delete(UUID id) {
        if (!installmentRepository.existsById(id))
            return ResponseEntity.notFound().build();
        installmentRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private boolean belongsToChit(Chit chit, Member member) {
        return chit != null && member != null && member.getChit().getId().equals(chit.getId());
    }

    private ResponseEntity<?> invalidHand() {
        return ResponseEntity.badRequest().body(Map.of("message", "Number of hand cannot exceed the chit duration."));
    }
}
