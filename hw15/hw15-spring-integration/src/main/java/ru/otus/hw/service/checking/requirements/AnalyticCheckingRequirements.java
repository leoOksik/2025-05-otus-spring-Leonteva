package ru.otus.hw.service.checking.requirements;

import org.springframework.stereotype.Component;
import ru.otus.hw.domain.LoanApplication;
import ru.otus.hw.service.checking.response.CheckResponse;

@Component
public class AnalyticCheckingRequirements implements Checking {

    @Override
    public CheckResponse check(LoanApplication loan) {
        if (loan.number() % 3 == 0) {
            return new CheckResponse(false, loan.number(),"Analytic check not passed");
        }
        return new CheckResponse(true, loan.number(),"Approved by analytic");
    }
}
