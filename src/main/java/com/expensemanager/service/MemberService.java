package com.expensemanager.service;

import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.expensemanager.dto.MemberRequest;
import com.expensemanager.dto.MemberResponse;
import com.expensemanager.entity.Chit;
import com.expensemanager.entity.Member;
import com.expensemanager.repository.ChitRepository;
import com.expensemanager.repository.InstallmentRepository;
import com.expensemanager.repository.MemberRepository;

@Service
public class MemberService {
    private final ChitRepository chitRepository;
    private final MemberRepository memberRepository;
    private final InstallmentRepository installmentRepository;

    public MemberService(ChitRepository chitRepository, MemberRepository memberRepository,
            InstallmentRepository installmentRepository) {
        this.chitRepository = chitRepository;
        this.memberRepository = memberRepository;
        this.installmentRepository = installmentRepository;
    }

    public List<MemberResponse> findAll(UUID chitId, boolean availableOnly) {
        List<Member> members = chitId == null ? memberRepository.findAllByOrderByNameAsc()
                : availableOnly ? memberRepository.findAllByChitIdAndChitTakenFalseOrderByNameAsc(chitId)
                        : memberRepository.findAllByChitIdOrderByNameAsc(chitId);
        return members.stream().map(MemberResponse::from).toList();
    }

    @Transactional
    public ResponseEntity<?> create(MemberRequest request) {
        Chit chit = chitRepository.findById(request.chitId()).orElse(null);
        if (chit == null)
            return ResponseEntity.notFound().build();
        if (memberRepository.countByChitId(chit.getId()) >= chit.getMemberCount()) {
            return ResponseEntity.badRequest().body(java.util.Map.of("message",
                    "Cannot add member. This chit already has all members assigned."));
        }
        Member member = new Member(chit, request.name(), request.mobileNumber(), request.email(),
                request.permanentAddress());
        return ResponseEntity.status(HttpStatus.CREATED).body(MemberResponse.from(memberRepository.save(member)));
    }

    @Transactional
    public ResponseEntity<MemberResponse> update(UUID id, MemberRequest request) {
        return memberRepository.findById(id).map(member -> chitRepository.findById(request.chitId()).map(chit -> {
            member.update(chit, request.name(), request.mobileNumber(), request.email(), request.permanentAddress());
            return ResponseEntity.ok(MemberResponse.from(memberRepository.save(member)));
        }).orElseGet(() -> ResponseEntity.notFound().build())).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Transactional
    public ResponseEntity<Void> delete(UUID id) {
        if (!memberRepository.existsById(id))
            return ResponseEntity.notFound().build();
        installmentRepository.deleteByMemberId(id);
        memberRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
