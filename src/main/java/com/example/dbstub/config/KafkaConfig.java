package com.example.dbstub.config;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {

    @Value("${app.kafka.topic.input:text-input}")
    private String inputTopic;

    @Value("${app.kafka.topic.output:text-output}")
    private String outputTopic;

    @Value("${app.kafka.topic.error:text-error}")
    private String errorTopic;

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        return new KafkaAdmin(configs);
    }

    @Bean
    public NewTopic inputTopic() {
        return new NewTopic(inputTopic, 3, (short) 1);
    }

    @Bean
    public NewTopic outputTopic() {
        return new NewTopic(outputTopic, 3, (short) 1);
    }

    @Bean
    public NewTopic errorTopic() {
        return new NewTopic(errorTopic, 1, (short) 1);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory(
            ConsumerFactory<String, String> consumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, String> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.getContainerProperties().setPollTimeout(5000);
        return factory;
    }
}
