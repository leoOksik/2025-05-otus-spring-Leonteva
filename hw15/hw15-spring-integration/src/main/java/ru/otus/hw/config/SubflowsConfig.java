package ru.otus.hw.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import ru.otus.hw.domain.LoanApplication;
import ru.otus.hw.service.checking.BankChecking;
import ru.otus.hw.service.checking.requirements.AnalyticCheckingRequirements;
import ru.otus.hw.service.checking.requirements.Checking;
import ru.otus.hw.service.checking.requirements.ManagerCheckingRequirements;
import ru.otus.hw.service.checking.requirements.SecurityCheckingRequirements;

@Configuration
@RequiredArgsConstructor
public class SubflowsConfig {
    private final BankChecking bankChecking;

    private final ManagerCheckingRequirements managerRequirements;

    private final SecurityCheckingRequirements securityRequirements;

    private final AnalyticCheckingRequirements analyticRequirements;

    @Bean
    public IntegrationFlow managerSubflow() {
        return subFlow("managerSubflowInput", "Manager",  managerRequirements);
    }

    @Bean
    public IntegrationFlow securitySubflow() {
        return subFlow("securitySubflowInput", "Security",  securityRequirements);
    }

    @Bean
    public IntegrationFlow analyticSubflow() {
        return subFlow("analyticSubflowInput", "Analytic",  analyticRequirements);
    }

    private IntegrationFlow subFlow(String nameChannel, String stage, Checking requirement) {
        return IntegrationFlow.from(nameChannel)
            .handle(LoanApplication.class, (loan, headers) ->
                    bankChecking.startChecking(loan, requirement, stage)).get();
    }
}
