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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import ru.otus.hw.listener.ProcessListener;
import ru.otus.hw.listener.WriteListener;
import ru.otus.hw.model.jpa.Genre;
import ru.otus.hw.model.mongo.GenreMongo;
import ru.otus.hw.processor.Cache;
import ru.otus.hw.processor.GenreItemProcessor;
import ru.otus.hw.repository.mongo.MongoGenreRepository;

@Configuration
@RequiredArgsConstructor
public class GenreBatchConfig {

    @Value("${batch.chunk}")
    private int chunk;

    private final JobRepository jobRepository;

    private final EntityManagerFactory entityManagerFactory;

    private final MongoGenreRepository mongoGenreRepository;

    @Bean
    public JpaPagingItemReader<Genre> genreReader() {
        return new JpaPagingItemReaderBuilder<Genre>()
            .name("genreReader")
            .entityManagerFactory(entityManagerFactory)
            .queryString("SELECT g FROM Genre g")
            .pageSize(chunk)
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
    public ProcessListener<Genre, GenreMongo> genreProcessListener() {
        return new ProcessListener<>();
    }

    @Bean
    public WriteListener<Genre, GenreMongo> genreWriteListener(
        Cache cache, ProcessListener<Genre, GenreMongo> processListener) {
        return new WriteListener<>(cache, processListener);
    }

    @Bean
    public Step genreStep(PlatformTransactionManager transactionManager,
                          JpaPagingItemReader<Genre> reader,
                          GenreItemProcessor processor,
                          RepositoryItemWriter<GenreMongo> writer,
                          ProcessListener<Genre, GenreMongo> processListener,
                          WriteListener<Genre, GenreMongo> writeListener) {
        return new StepBuilder("genreStep", jobRepository)
            .<Genre, GenreMongo>chunk(chunk, transactionManager)
            .reader(reader)
            .processor(processor)
            .writer(writer)
            .listener(processListener)
            .listener(writeListener)
            .build();
    }
}
