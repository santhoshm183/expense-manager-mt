package com.expensemanager.repository;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import com.expensemanager.entity.Member;

public interface MemberRepository extends JpaRepository<Member, UUID> {
    @EntityGraph(attributePaths = "chit")
    List<Member> findAllByOrderByNameAsc();

    @EntityGraph(attributePaths = "chit")
    List<Member> findAllByChitIdOrderByNameAsc(UUID chitId);

    long deleteByChitId(UUID chitId);
}
