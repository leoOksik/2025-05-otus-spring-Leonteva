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
import ru.otus.hw.model.jpa.Author;
import ru.otus.hw.model.mongo.AuthorMongo;
import ru.otus.hw.processor.AuthorItemProcessor;
import ru.otus.hw.repository.mongo.MongoAuthorRepository;

@Configuration
@RequiredArgsConstructor
public class AuthorBatchConfig {

    private static final int CHUNK = 5;

    private final JobRepository jobRepository;

    private final EntityManagerFactory entityManagerFactory;

    private final MongoAuthorRepository mongoAuthorRepository;

    @Bean
    public JpaPagingItemReader<Author> authorReader() {
        return new JpaPagingItemReaderBuilder<Author>()
            .name("authorReader")
            .entityManagerFactory(entityManagerFactory)
            .queryString("SELECT a FROM Author a")
            .pageSize(5)
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
    public Step authorStep(PlatformTransactionManager transactionManager,
                           @Qualifier("authorReader") JpaPagingItemReader<Author> reader,
                           @Qualifier("authorProcessor") AuthorItemProcessor processor,
                           @Qualifier("authorWriter") RepositoryItemWriter<AuthorMongo> writer) {

        return new StepBuilder("authorStep", jobRepository)
            .<Author, AuthorMongo>chunk(CHUNK, transactionManager)
            .reader(reader)
            .processor(processor)
            .writer(writer)
            .build();
    }
}
