package com.example.dbstub.kafka;

import com.example.dbstub.dto.KafkaMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@SpringBootTest
@EmbeddedKafka(
    topics = {"text-input", "text-output", "text-error"},
    partitions = 1,
    brokerProperties = {"listeners=PLAINTEXT://localhost:9092"}
)
class KafkaIntegrationTest {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    @Autowired
    private ObjectMapper objectMapper;

    private Map<String, Object> consumerProps;

    @BeforeEach
    void setUp() {
        consumerProps = KafkaTestUtils.consumerProps("test-group", "true", embeddedKafkaBroker);
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    }

    @DynamicPropertySource
    static void kafkaProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", () -> "localhost:9092");
        registry.add("spring.kafka.consumer.group-id", () -> "test-group");
        registry.add("app.kafka.poll-interval", () -> "1000");
    }

    @Test
    void testProcessValidText() throws Exception {
        String messageId = UUID.randomUUID().toString();
        KafkaMessage request = new KafkaMessage();
        request.setId(messageId);
        request.setText("Что такое искусственный интеллект?");

        String json = objectMapper.writeValueAsString(request);
        kafkaTemplate.send("text-input", messageId, json);

        var consumer = new DefaultKafkaConsumerFactory<String, String>(
            consumerProps,
            new StringDeserializer(),
            new StringDeserializer()
        ).createConsumer();

        embeddedKafkaBroker.consumeFromAnEmbeddedTopic(consumer, "text-output");

        await().untilAsserted(() -> {
            ConsumerRecord<String, String> record = KafkaTestUtils.getSingleRecord(consumer, "text-output");
            assertThat(record).isNotNull();

            KafkaMessage response = objectMapper.readValue(record.value(), KafkaMessage.class);
            assertThat(response.getId()).isEqualTo(messageId);
            assertThat(response.getStatus()).isEqualTo("SUCCESS");
        });

        consumer.close();
    }

    @Test
    void testProcessInvalidText() throws Exception {
        String messageId = UUID.randomUUID().toString();
        KafkaMessage request = new KafkaMessage();
        request.setId(messageId);
        request.setText("12345");

        String json = objectMapper.writeValueAsString(request);
        kafkaTemplate.send("text-input", messageId, json);

        var consumer = new DefaultKafkaConsumerFactory<String, String>(
            consumerProps,
            new StringDeserializer(),
            new StringDeserializer()
        ).createConsumer();

        embeddedKafkaBroker.consumeFromAnEmbeddedTopic(consumer, "text-error");

        await().untilAsserted(() -> {
            ConsumerRecord<String, String> record = KafkaTestUtils.getSingleRecord(consumer, "text-error");
            assertThat(record).isNotNull();

            KafkaMessage response = objectMapper.readValue(record.value(), KafkaMessage.class);
            assertThat(response.getId()).isEqualTo(messageId);
            assertThat(response.getStatus()).isEqualTo("ERROR");
            assertThat(response.getError()).contains("Invalid text");
        });

        consumer.close();
    }
}
