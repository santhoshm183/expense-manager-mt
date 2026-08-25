package com.expensemanager.dto;

import java.util.UUID;
import com.expensemanager.entity.Member;

public record MemberResponse(
        UUID id,
        ChitSummary chit,
        String name,
        String mobileNumber,
        String email,
        String permanentAddress,
        boolean chitTaken) {

    public record ChitSummary(UUID id, String name) {
    }

    public static MemberResponse from(Member member) {
        return new MemberResponse(
                member.getId(),
                new ChitSummary(member.getChit().getId(), member.getChit().getName()),
                member.getName(),
                member.getMobileNumber(),
                member.getEmail(),
                member.getPermanentAddress(),
                member.isChitTaken());
    }
}
