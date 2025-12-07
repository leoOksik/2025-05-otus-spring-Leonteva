package ru.otus.hw.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.integration.channel.PublishSubscribeChannel;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ru.otus.hw.AppRunner;
import ru.otus.hw.domain.FinalDecision;
import ru.otus.hw.domain.LoanApplication;
import ru.otus.hw.domain.LoanType;
import ru.otus.hw.service.BankGateway;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class FlowConfigTest {

    @MockitoBean
    private AppRunner appRunner;

    @Autowired
    private BankGateway bankGateway;

    @Autowired
    private MessageChannel finalDecisionChannel;

    private List<LoanApplication> applications;

    private List<FinalDecision> expectedDecisions;

    @BeforeEach
    public void setup() {
        applications = List.of(
            new LoanApplication(1L, 10L, 300_000,
                50_000, 12, LoanType.CONSUMER),
            new LoanApplication(2L, 20L, 800_000,
                60_000, 24, LoanType.EDUCATIONAL)
        );

        expectedDecisions = List.of(
            new FinalDecision(1L, 1L, true),
            new FinalDecision(2L, 2L, false)
        );
    }

    @Test
    void shouldRunCheckProcessApplicationAndReturnCorrectlyDecision() {

        List<FinalDecision> actualDecisions = new CopyOnWriteArrayList<>();
        MessageHandler handler = msg -> actualDecisions.add((FinalDecision) msg.getPayload());

        PublishSubscribeChannel subscribeChannel = (PublishSubscribeChannel) finalDecisionChannel;
        subscribeChannel.subscribe(handler);

        bankGateway.process(applications);

        assertThat(expectedDecisions).usingRecursiveComparison()
            .ignoringFields("number").isEqualTo(actualDecisions);

    }
}
