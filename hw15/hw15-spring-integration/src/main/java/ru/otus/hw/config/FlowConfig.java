package ru.otus.hw.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;

import ru.otus.hw.domain.FinalDecision;
import ru.otus.hw.service.checking.response.CheckResponse;

import java.util.List;
import java.util.Random;


@Configuration
@RequiredArgsConstructor
public class FlowConfig {

    @Bean
    public IntegrationFlow loanFlow() {
        return f -> f
            .channel("loanInputChannel")
            .split()
            .scatterGather(
                scatterer -> scatterer
                    .applySequence(false)
                    .recipient("managerSubflowInput")
                    .recipient("securitySubflowInput")
                    .recipient("analyticSubflowInput"),
                gatherer -> gatherer
                    .correlationExpression("payload.applicationId")
                    .releaseStrategy(group -> group.size() == 3)
            )
            .transform(this::toFinalDecision)
            .channel("finalDecisionChannel");
    }

    private FinalDecision toFinalDecision(List<CheckResponse> responses) {
        boolean verified = responses.stream().allMatch(CheckResponse::verified);
        return new FinalDecision(
            new Random().nextLong(1000),
            responses.get(0).applicationId(),
            verified
        );
    }
}
