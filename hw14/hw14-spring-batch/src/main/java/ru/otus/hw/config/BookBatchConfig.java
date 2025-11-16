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
import ru.otus.hw.model.jpa.Book;
import ru.otus.hw.model.mongo.BookMongo;
import ru.otus.hw.processor.BookItemProcessor;
import ru.otus.hw.repository.mongo.MongoBookRepository;

@Configuration
@RequiredArgsConstructor
public class BookBatchConfig {

    private static final int CHUNK = 5;

    private final JobRepository jobRepository;

    private final EntityManagerFactory entityManagerFactory;

    private final MongoBookRepository mongoBookRepository;

    @Bean
    public JpaPagingItemReader<Book> bookReader() {
        return new JpaPagingItemReaderBuilder<Book>()
            .name("bookReader")
            .entityManagerFactory(entityManagerFactory)
            .queryString("SELECT b FROM Book b")
            .pageSize(5)
            .build();
    }

    @Bean
    public BookItemProcessor bookProcessor() {
        return new BookItemProcessor();
    }

    @Bean
    public RepositoryItemWriter<BookMongo> bookWriter() {
        return new RepositoryItemWriterBuilder<BookMongo>()
            .repository(mongoBookRepository)
            .methodName("save")
            .build();
    }

    @Bean
    public Step bookStep(PlatformTransactionManager transactionManager,
                         @Qualifier("bookReader") JpaPagingItemReader<Book> reader,
                         @Qualifier("bookProcessor") BookItemProcessor processor,
                         @Qualifier("bookWriter") RepositoryItemWriter<BookMongo> writer) {

        return new StepBuilder("bookStep", jobRepository)
            .<Book, BookMongo>chunk(CHUNK, transactionManager)
            .reader(reader)
            .processor(processor)
            .writer(writer)
            .build();
    }
}
