package ru.otus.hw.service.checking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.otus.hw.domain.LoanApplication;
import ru.otus.hw.logger.DecisionLogger;
import ru.otus.hw.service.checking.requirements.Checking;
import ru.otus.hw.service.checking.response.CheckResponse;


@Slf4j
@RequiredArgsConstructor
@Component
public class BankChecking {

    private final DecisionLogger decisionLog;

    public CheckResponse startChecking(LoanApplication loan, Checking requirements, String stage) {
        try {
            Thread.sleep(500);
            CheckResponse result = requirements.check(loan);
            if (result == null) {
                return new CheckResponse(false, loan.number(), "Result checking requirements is null");
            }
            decisionLog.logDecision(stage, loan.number(), result.verified(), result.comment());
            return result;
        } catch (InterruptedException e) {
            return new CheckResponse(false, loan.number(), "Interrupted check");
        }
    }
}
