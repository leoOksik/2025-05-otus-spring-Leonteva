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
import ru.otus.hw.model.jpa.Comment;
import ru.otus.hw.model.mongo.CommentMongo;
import ru.otus.hw.processor.Cache;
import ru.otus.hw.processor.CommentItemProcessor;
import ru.otus.hw.repository.jpa.JpaBookRepository;
import ru.otus.hw.repository.mongo.MongoCommentRepository;

@Configuration
@RequiredArgsConstructor
public class CommentBatchConfig {

    @Value("${batch.chunk}")
    private int chunk;

    private final JobRepository jobRepository;

    private final EntityManagerFactory entityManagerFactory;

    private final JpaBookRepository jpaBookRepository;

    private final MongoCommentRepository mongoCommentRepository;

    @Bean
    public JpaPagingItemReader<Comment> commentReader() {
        return new JpaPagingItemReaderBuilder<Comment>()
            .name("commentReader")
            .entityManagerFactory(entityManagerFactory)
            .queryString("SELECT c FROM Comment c")
            .pageSize(chunk)
            .build();
    }

    @Bean
    public CommentItemProcessor commentProcessor(Cache cache) {
        return new CommentItemProcessor(jpaBookRepository, cache);
    }

    @Bean
    public RepositoryItemWriter<CommentMongo> commentWriter() {
        return new RepositoryItemWriterBuilder<CommentMongo>()
            .repository(mongoCommentRepository)
            .methodName("save")
            .build();
    }

    @Bean
    public ProcessListener<Comment, CommentMongo> commentProcessListener() {
        return new ProcessListener<>();
    }

    @Bean
    public WriteListener<Comment, CommentMongo> commentWriteListener(
        Cache cache, ProcessListener<Comment, CommentMongo> processListener) {
        return new WriteListener<>(cache, processListener);
    }

    @Bean
    public Step commentStep(PlatformTransactionManager transactionManager,
                            JpaPagingItemReader<Comment> reader,
                            CommentItemProcessor processor,
                            RepositoryItemWriter<CommentMongo> writer,
                            ProcessListener<Comment, CommentMongo> processListener,
                            WriteListener<Comment, CommentMongo> writeListener) {
        return new StepBuilder("commentStep", jobRepository)
            .<Comment, CommentMongo>chunk(chunk, transactionManager)
            .reader(reader)
            .processor(processor)
            .writer(writer)
            .listener(processListener)
            .listener(writeListener)
            .build();
    }
}
