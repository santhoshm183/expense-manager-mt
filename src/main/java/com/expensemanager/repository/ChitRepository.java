package com.expensemanager.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.expensemanager.entity.Chit;

public interface ChitRepository extends JpaRepository<Chit, java.util.UUID> {
    List<Chit> findAllByOrderByStartDateDesc();

    long countByStatus(com.expensemanager.entity.ChitStatus status);
}
