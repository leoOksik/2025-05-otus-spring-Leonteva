package ru.otus.hw.service;

import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;
import ru.otus.hw.domain.FinalDecision;
import ru.otus.hw.domain.LoanApplication;

import java.util.Collection;

@MessagingGateway
public interface BankGateway {

    @Gateway(requestChannel = "loanInputChannel")
    Collection<FinalDecision> process(Collection<LoanApplication> loans);
}
