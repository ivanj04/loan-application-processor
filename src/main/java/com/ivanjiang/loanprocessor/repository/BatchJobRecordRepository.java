package com.ivanjiang.loanprocessor.repository;

import com.ivanjiang.loanprocessor.model.BatchJobRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BatchJobRecordRepository extends JpaRepository<BatchJobRecord, Long> {

    Optional<BatchJobRecord> findByBatchId(String batchId);
}
