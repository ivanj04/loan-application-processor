package com.ivanjiang.loanprocessor.batch;

import com.ivanjiang.loanprocessor.model.LoanApplication;
import com.ivanjiang.loanprocessor.repository.LoanApplicationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

/**
 * Persists processed loan applications via Spring Data JPA.
 * Uses saveAll for efficient batch insert.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class LoanItemWriter implements ItemWriter<LoanApplication> {

    private final LoanApplicationRepository loanApplicationRepository;

    @Override
    public void write(Chunk<? extends LoanApplication> chunk) {
        log.debug("Writing chunk of {} loan applications", chunk.size());
        loanApplicationRepository.saveAll(chunk.getItems());
    }
}
