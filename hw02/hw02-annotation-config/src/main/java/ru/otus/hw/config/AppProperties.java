package ru.otus.hw.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public record AppProperties(int rightAnswersCountToPass,
                            String testFileName) implements TestConfig, TestFileNameProvider {

    public AppProperties(@Value("${test.rightAnswersCountToPass}") int rightAnswersCountToPass,
                         @Value("${test.fileName}") String testFileName) {
        this.rightAnswersCountToPass = rightAnswersCountToPass;
        this.testFileName = testFileName;
    }
}
