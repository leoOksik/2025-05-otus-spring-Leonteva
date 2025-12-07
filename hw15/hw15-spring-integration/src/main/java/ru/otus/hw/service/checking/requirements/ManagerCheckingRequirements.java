package ru.otus.hw.service.checking.requirements;

import org.springframework.stereotype.Component;
import ru.otus.hw.domain.LoanApplication;
import ru.otus.hw.service.checking.response.CheckResponse;

@Component
public class ManagerCheckingRequirements implements Checking {

    @Override
    public CheckResponse check(LoanApplication loan) {
        if (loan.income() < 30_000) {
            return new CheckResponse(false, loan.number(), "Income too low");
        }
        return new CheckResponse(true, loan.number(), "Approved by manager");
    }
}
