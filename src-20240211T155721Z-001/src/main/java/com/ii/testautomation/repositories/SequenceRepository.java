package com.ii.testautomation.repositories;

import com.ii.testautomation.entities.Sequence;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SequenceRepository extends JpaRepository<Sequence, Long> {
    List<Sequence> findBySchedulingCode(String scheduleCode);
}
