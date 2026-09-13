package com.expensemanager.contoller;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
import com.expensemanager.dto.ChitDashboardResponse;
import com.expensemanager.dto.ChitRequest;
import com.expensemanager.entity.Chit;
import com.expensemanager.service.ChitService;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ChitController {
    private final ChitService chitService;

    public ChitController(ChitService chitService) {
        this.chitService = chitService;
    }

    @GetMapping("/chits")
    public List<Chit> chits(@RequestParam(required = false) UUID chitId) {
        if (chitId == null) {
            return chitService.findAll();
        }
        return chitService.findAll().stream()
                .filter(item -> item.getId().equals(chitId))
                .toList();
    }

    @GetMapping("/chits/{id}/summary")
    public ResponseEntity<ChitDashboardResponse> chitSummary(@PathVariable UUID id) {
        return chitService.summary(id);
    }

    @PostMapping("/chits")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Chit> createChit(@Valid @RequestBody ChitRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(chitService.create(request));
    }

    @PutMapping("/chits/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Chit> updateChit(@PathVariable UUID id, @Valid @RequestBody ChitRequest request) {
        return chitService.update(id, request);
    }

    @DeleteMapping("/chits/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteChit(@PathVariable UUID id) {
        return chitService.delete(id);
    }

    @GetMapping("/chit-summary")
    public Map<String, Object> summary() {
        return chitService.totals();
    }
}
