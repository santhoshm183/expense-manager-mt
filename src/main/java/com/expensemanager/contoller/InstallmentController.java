package com.expensemanager.contoller;

import java.util.List;
import java.util.UUID;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.expensemanager.dto.InstallmentRequest;
import com.expensemanager.dto.InstallmentResponse;
import com.expensemanager.service.InstallmentService;

@RestController
@RequestMapping("/api/installments")
@CrossOrigin(origins = "*")
public class InstallmentController {
    private final InstallmentService installmentService;

    public InstallmentController(InstallmentService installmentService) {
        this.installmentService = installmentService;
    }

    @GetMapping
    public List<InstallmentResponse> installments(@RequestParam(required = false) UUID chitId,
            @RequestParam(required = false) UUID memberId) {
        return installmentService.findAll(chitId, memberId);
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody InstallmentRequest request) {
        return installmentService.create(request);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @Valid @RequestBody InstallmentRequest request) {
        return installmentService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        return installmentService.delete(id);
    }
}
