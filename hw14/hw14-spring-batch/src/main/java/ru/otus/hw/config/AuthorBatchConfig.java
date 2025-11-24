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
import ru.otus.hw.model.jpa.Author;
import ru.otus.hw.model.mongo.AuthorMongo;
import ru.otus.hw.processor.AuthorItemProcessor;
import ru.otus.hw.processor.Cache;
import ru.otus.hw.repository.mongo.MongoAuthorRepository;

@Configuration
@RequiredArgsConstructor
public class AuthorBatchConfig {

    @Value("${batch.chunk}")
    private int chunk;

    private final JobRepository jobRepository;

    private final EntityManagerFactory entityManagerFactory;

    private final MongoAuthorRepository mongoAuthorRepository;

    @Bean
    public Cache cache() {
        return new Cache();
    }

    @Bean
    public JpaPagingItemReader<Author> authorReader() {
        return new JpaPagingItemReaderBuilder<Author>()
            .name("authorReader")
            .entityManagerFactory(entityManagerFactory)
            .queryString("SELECT a FROM Author a")
            .pageSize(chunk)
            .build();
    }

    @Bean
    public AuthorItemProcessor authorProcessor() {
        return new AuthorItemProcessor();
    }

    @Bean
    public RepositoryItemWriter<AuthorMongo> authorWriter() {
        return new RepositoryItemWriterBuilder<AuthorMongo>()
            .repository(mongoAuthorRepository)
            .methodName("save")
            .build();
    }

    @Bean
    public ProcessListener<Author, AuthorMongo> authorProcessListener() {
        return new ProcessListener<>();
    }

    @Bean
    public WriteListener<Author, AuthorMongo> authorWriteListener(
        Cache cache, ProcessListener<Author, AuthorMongo> processListener) {
        return new WriteListener<>(cache, processListener);
    }

    @Bean
    public Step authorStep(PlatformTransactionManager transactionManager,
                           JpaPagingItemReader<Author> reader,
                           AuthorItemProcessor processor,
                           RepositoryItemWriter<AuthorMongo> writer,
                           ProcessListener<Author, AuthorMongo> processListener,
                           WriteListener<Author, AuthorMongo> writeListener) {
        return new StepBuilder("authorStep", jobRepository)
            .<Author, AuthorMongo>chunk(chunk, transactionManager)
            .reader(reader)
            .processor(processor)
            .writer(writer)
            .listener(processListener)
            .listener(writeListener)
            .build();
    }
}
