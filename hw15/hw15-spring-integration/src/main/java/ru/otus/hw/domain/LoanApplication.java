package ru.otus.hw.domain;

public record LoanApplication(Long number, Long clientId, Integer loanAmount,
                              Integer income, Integer loanPeriod, LoanType loanType) {
}
