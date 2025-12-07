package ru.otus.hw.logger;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class DecisionLogger {

    public void logDecision(String stage, Long applicationNumber, Boolean verified, String comment) {
        log.info(
            "Check {} for application №  {} ->  verified: {} comment: {}",
            stage, applicationNumber, verified, comment
        );
    }
}
