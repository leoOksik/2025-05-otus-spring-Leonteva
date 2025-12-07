package ru.otus.hw.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.channel.PublishSubscribeChannel;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.messaging.MessageChannel;

@Configuration
public class ChannelConfig {

    @Bean
    public MessageChannel loanInputChannel() {
        return new QueueChannel(100);
    }

    @Bean
    public MessageChannel finalDecisionChannel() {
        return new PublishSubscribeChannel();
    }

    @Bean
    public MessageChannel managerSubflowInput() {
        return new DirectChannel();
    }

    @Bean
    public MessageChannel securitySubflowInput() {
        return new DirectChannel();
    }

    @Bean
    public MessageChannel analyticSubflowInput() {
        return new DirectChannel();
    }
}
