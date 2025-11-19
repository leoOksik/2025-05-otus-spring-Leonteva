package ru.otus.hw.config;

import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.data.builder.RepositoryItemWriterBuilder;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import ru.otus.hw.model.jpa.Genre;
import ru.otus.hw.model.mongo.GenreMongo;
import ru.otus.hw.processor.GenreItemProcessor;
import ru.otus.hw.repository.mongo.MongoGenreRepository;

@Configuration
@RequiredArgsConstructor
public class GenreBatchConfig {

    private static final int CHUNK = 5;

    private final JobRepository jobRepository;

    private final EntityManagerFactory entityManagerFactory;

    private final MongoGenreRepository mongoGenreRepository;

    @Bean
    public JpaPagingItemReader<Genre> genreReader() {
        return new JpaPagingItemReaderBuilder<Genre>()
            .name("genreReader")
            .entityManagerFactory(entityManagerFactory)
            .queryString("SELECT g FROM Genre g")
            .pageSize(5)
            .build();
    }

    @Bean
    public GenreItemProcessor genreProcessor() {
        return new GenreItemProcessor();
    }

    @Bean
    public RepositoryItemWriter<GenreMongo> genreWriter() {
        return new RepositoryItemWriterBuilder<GenreMongo>()
            .repository(mongoGenreRepository)
            .methodName("save")
            .build();
    }

    @Bean
    public Step genreStep(PlatformTransactionManager transactionManager,
                          @Qualifier("genreReader") JpaPagingItemReader<Genre> reader,
                          @Qualifier("genreProcessor") GenreItemProcessor processor,
                          @Qualifier("genreWriter") RepositoryItemWriter<GenreMongo> writer) {

        return new StepBuilder("genreStep", jobRepository)
            .<Genre, GenreMongo>chunk(CHUNK, transactionManager)
            .reader(reader)
            .processor(processor)
            .writer(writer)
            .build();
    }
}
