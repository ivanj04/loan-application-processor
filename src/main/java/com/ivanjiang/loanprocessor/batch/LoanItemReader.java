package com.ivanjiang.loanprocessor.batch;

import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.core.io.Resource;

/**
 * FlatFileItemReader for parsing CSV loan application files.
 * Skips the header line and maps each row to a LoanCsvRecord.
 */
public class LoanItemReader extends FlatFileItemReader<LoanCsvRecord> {

    public LoanItemReader(Resource resource) {
        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
        tokenizer.setNames("applicationId", "borrowerName", "loanAmount", "propertyValue",
                "annualIncome", "creditScore", "loanType");

        BeanWrapperFieldSetMapper<LoanCsvRecord> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(LoanCsvRecord.class);

        DefaultLineMapper<LoanCsvRecord> lineMapper = new DefaultLineMapper<>();
        lineMapper.setLineTokenizer(tokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);

        setResource(resource);
        setLinesToSkip(1);
        setLineMapper(lineMapper);
        setName("loanItemReader");
    }
}
