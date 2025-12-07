package ru.otus.hw.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.stereotype.Component;
import ru.otus.hw.domain.FinalDecision;

@Slf4j
@Component
public class FinalDecisionListener {

    @ServiceActivator(inputChannel = "finalDecisionChannel")
    public void handle(FinalDecision decision) {
        log.info("Final decision: number={}, applicationId={}, approved={} ",
            decision.number(), decision.applicationId(), decision.approved());
    }
}
