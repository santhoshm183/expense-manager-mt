package com.expensemanager.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import com.expensemanager.entity.Installment;

public record InstallmentResponse(UUID id, UUID chitId, String chitName, UUID memberId, String memberName,
        Integer numberOfHand, BigDecimal installmentAmount, LocalDate installmentDate) {
    public static InstallmentResponse from(Installment installment) {
        return new InstallmentResponse(installment.getId(), installment.getChit().getId(),
                installment.getChit().getName(),
                installment.getMember().getId(), installment.getMember().getName(), installment.getNumberOfHand(),
                installment.getInstallmentAmount(),
                installment.getInstallmentDate());
    }
}
