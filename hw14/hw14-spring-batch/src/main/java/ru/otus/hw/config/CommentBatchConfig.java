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
import ru.otus.hw.model.jpa.Comment;
import ru.otus.hw.model.mongo.CommentMongo;
import ru.otus.hw.processor.CommentItemProcessor;
import ru.otus.hw.repository.jpa.JpaBookRepository;
import ru.otus.hw.repository.mongo.MongoCommentRepository;

@Configuration
@RequiredArgsConstructor
public class CommentBatchConfig {

    private static final int CHUNK = 5;

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
            .pageSize(5)
            .build();
    }

    @Bean
    public CommentItemProcessor commentProcessor() {
        return new CommentItemProcessor(jpaBookRepository);
    }

    @Bean
    public RepositoryItemWriter<CommentMongo> commentWriter() {
        return new RepositoryItemWriterBuilder<CommentMongo>()
            .repository(mongoCommentRepository)
            .methodName("save")
            .build();
    }

    @Bean
    public Step commentStep(PlatformTransactionManager transactionManager,
                            @Qualifier("commentReader") JpaPagingItemReader<Comment> reader,
                            @Qualifier("commentProcessor") CommentItemProcessor processor,
                            @Qualifier("commentWriter") RepositoryItemWriter<CommentMongo> writer) {

        return new StepBuilder("commentStep", jobRepository)
            .<Comment, CommentMongo>chunk(CHUNK, transactionManager)
            .reader(reader)
            .processor(processor)
            .writer(writer)
            .build();
    }
}
