package com.ivanjiang.loanprocessor.batch;

import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.core.io.Resource;

/**
 * FlatFileItemReader for parsing CSV loan application files.
 * Skips the header line and maps each row to a LoanCsvRecord.
 */
public class LoanItemReader extends FlatFileItemReader<LoanCsvRecord> {

    public LoanItemReader(Resource resource) {
        FlatFileItemReader<LoanCsvRecord> delegate = new FlatFileItemReaderBuilder<LoanCsvRecord>()
                .name("loanItemReader")
                .resource(resource)
                .linesToSkip(1)
                .delimited()
                .names("applicationId", "borrowerName", "loanAmount", "propertyValue",
                       "annualIncome", "creditScore", "loanType")
                .fieldSetMapper(new BeanWrapperFieldSetMapper<>() {{
                    setTargetType(LoanCsvRecord.class);
                }})
                .build();

        setResource(resource);
        setLinesToSkip(1);
        setLineMapper(delegate.getLineMapper());
        setName("loanItemReader");
    }
}
