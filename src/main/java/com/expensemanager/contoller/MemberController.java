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
import com.expensemanager.dto.MemberRequest;
import com.expensemanager.dto.MemberResponse;
import com.expensemanager.service.MemberService;

@RestController
@RequestMapping("/api/members")
@CrossOrigin(origins = "*")
public class MemberController {
    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping
    public List<MemberResponse> members(@RequestParam(required = false) UUID chitId,
            @RequestParam(defaultValue = "false") boolean availableOnly) {
        return memberService.findAll(chitId, availableOnly);
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody MemberRequest request) {
        return memberService.create(request);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MemberResponse> update(@PathVariable UUID id, @Valid @RequestBody MemberRequest request) {
        return memberService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        return memberService.delete(id);
    }
}
