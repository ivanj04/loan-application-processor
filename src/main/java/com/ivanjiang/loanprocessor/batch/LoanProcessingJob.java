package com.ivanjiang.loanprocessor.batch;

import com.ivanjiang.loanprocessor.model.LoanApplication;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.support.CompositeItemProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.List;

/**
 * Spring Batch 5 job configuration for loan processing pipeline.
 *
 * Pipeline:
 *   CSV → LoanValidationProcessor → LoanScoringProcessor → LoanClassificationProcessor → DB
 */
@Configuration
@RequiredArgsConstructor
public class LoanProcessingJob {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final LoanValidationProcessor validationProcessor;
    private final LoanScoringProcessor scoringProcessor;
    private final LoanClassificationProcessor classificationProcessor;
    private final LoanItemWriter loanItemWriter;
    private final JobCompletionListener jobCompletionListener;

    @Bean
    public Job loanProcessingJob() {
        return new JobBuilder("loanProcessingJob", jobRepository)
                .listener(jobCompletionListener)
                .start(loanProcessingStep())
                .build();
    }

    @Bean
    public Step loanProcessingStep() {
        return new StepBuilder("loanProcessingStep", jobRepository)
                .<LoanCsvRecord, LoanApplication>chunk(10, transactionManager)
                .reader(loanItemReader())
                .processor(compositeProcessor())
                .writer(loanItemWriter)
                .build();
    }

    @Bean
    @org.springframework.batch.core.configuration.annotation.StepScope
    public LoanItemReader loanItemReader() {
        // The actual resource is set dynamically in JobService via job parameters
        return new LoanItemReader(new FileSystemResource("placeholder.csv"));
    }

    @Bean
    public CompositeItemProcessor<LoanCsvRecord, LoanApplication> compositeProcessor() {
        CompositeItemProcessor<LoanCsvRecord, LoanApplication> processor = new CompositeItemProcessor<>();
        processor.setDelegates(List.of(validationProcessor, scoringProcessor, classificationProcessor));
        return processor;
    }
}
