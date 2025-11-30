package ru.otus.hw;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ru.otus.hw.domain.LoanApplication;
import ru.otus.hw.domain.LoanType;
import ru.otus.hw.service.BankGateway;

import java.util.Random;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor

public class AppRunner implements CommandLineRunner {

    private static final int LOAN_APP_COUNT = 50;

    private final BankGateway gateway;

    private final Random random = new Random();

    @Override
    public void run(String... args) {
        log.info("---Start processing---");
        var loanApplications = generateApplications();
        gateway.process(loanApplications);
    }

    private List<LoanApplication> generateApplications() {
        LoanType[] loanTypes = LoanType.values();
        return random
            .ints(AppRunner.LOAN_APP_COUNT)
            .mapToObj(i -> new LoanApplication(
                random.nextLong(300),
                random.nextLong(300),
                random.nextInt(300_000, 10_000_000),
                random.nextInt(20_000, 2_000_000),
                random.nextInt(6, 60),
                loanTypes[random.nextInt(loanTypes.length)]
            ))
            .toList();
    }
}
