package ru.otus.hw.service.checking.requirements;

import ru.otus.hw.domain.LoanApplication;
import ru.otus.hw.service.checking.response.CheckResponse;

public interface Checking {
    CheckResponse check(LoanApplication loan);
}
