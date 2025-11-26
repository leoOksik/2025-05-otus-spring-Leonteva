package ru.otus.hw.config;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class JobBatchConfig {

    private final JobRepository jobRepository;

    private final Step authorStep;

    private final Step genreStep;

    private final Step bookStep;

    private final Step commentStep;

    @Bean
    public Job jobMigration() {
        return new JobBuilder("migration", jobRepository)
            .incrementer(new RunIdIncrementer())
            .start(authorStep)
            .next(genreStep)
            .next(bookStep)
            .next(commentStep)
            .build();
    }
}
