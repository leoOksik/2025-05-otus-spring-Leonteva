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
import ru.otus.hw.model.jpa.Book;
import ru.otus.hw.model.mongo.BookMongo;
import ru.otus.hw.processor.BookItemProcessor;
import ru.otus.hw.processor.Cache;
import ru.otus.hw.repository.mongo.MongoBookRepository;


@Configuration
@RequiredArgsConstructor
public class BookBatchConfig {

    @Value("${batch.chunk}")
    private int chunk;

    private final JobRepository jobRepository;

    private final EntityManagerFactory entityManagerFactory;

    private final MongoBookRepository mongoBookRepository;

    @Bean
    public JpaPagingItemReader<Book> bookReader() {
        return new JpaPagingItemReaderBuilder<Book>()
            .name("bookReader")
            .entityManagerFactory(entityManagerFactory)
            .queryString("SELECT b FROM Book b")
            .pageSize(chunk)
            .build();
    }

    @Bean
    public BookItemProcessor bookProcessor(Cache cache) {
        return new BookItemProcessor(cache);
    }

    @Bean
    public RepositoryItemWriter<BookMongo> bookWriter() {
        return new RepositoryItemWriterBuilder<BookMongo>()
            .repository(mongoBookRepository)
            .methodName("save")
            .build();
    }

    @Bean
    public ProcessListener<Book, BookMongo> bookProcessListener() {
        return new ProcessListener<>();
    }

    @Bean
    public WriteListener<Book, BookMongo> bookWriteListener(
        Cache cache, ProcessListener<Book, BookMongo> processListener) {
        return new WriteListener<>(cache, processListener);
    }

    @Bean
    public Step bookStep(PlatformTransactionManager transactionManager,
                         JpaPagingItemReader<Book> reader,
                         BookItemProcessor processor,
                         RepositoryItemWriter<BookMongo> writer,
                         ProcessListener <Book, BookMongo> processListener,
                         WriteListener <Book, BookMongo> writeListener) {
        return new StepBuilder("bookStep", jobRepository)
            .<Book, BookMongo>chunk(chunk, transactionManager)
            .reader(reader)
            .processor(processor)
            .writer(writer)
            .listener(processListener)
            .listener(writeListener)
            .build();
    }
}
