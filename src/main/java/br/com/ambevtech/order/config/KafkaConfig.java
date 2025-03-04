package br.com.ambevtech.order.config;

import br.com.ambevtech.order.api.model.OrderEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;
import lombok.NonNull;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties.AckMode;
import org.springframework.kafka.support.LogIfLevelEnabled.Level;
import org.springframework.kafka.support.serializer.JsonDeserializer;

@Configuration
public class KafkaConfig {

    public static final String CONTAINER_FACTORY_BEAN_NAME = "consumerFactory";
    public static final String SAVE_ORDER_TOPIC = "SAVE_ORDER_TOPIC";

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServer;
    @Value("${spring.application.name}")
    private String applicationName;

    @Autowired
    private ObjectMapper objectMapper;

    @Bean
    public NewTopic saveOrder() {
        return new NewTopic(SAVE_ORDER_TOPIC, 6, (short)3);
    }


    @Bean(CONTAINER_FACTORY_BEAN_NAME)
    public ConcurrentKafkaListenerContainerFactory<String, OrderEvent> kafkaListenerContainerFactory() {
        int maxPollRecords = 10;
        ConcurrentKafkaListenerContainerFactory<String, OrderEvent> factory = new ConcurrentKafkaListenerContainerFactory();
        Map<String, Object> props = new HashMap();
        props.put("enable.auto.commit", false);
        props.put("request.timeout.ms", 30000);
        props.put("heartbeat.interval.ms", 1000);
        props.put("max.poll.interval.ms", 900000);
        props.put("max.poll.records", maxPollRecords);
        props.put("session.timeout.ms", 30000);
        factory.getContainerProperties().setMissingTopicsFatal(false);
        factory.getContainerProperties().setAckMode(AckMode.MANUAL_IMMEDIATE);
        factory.getContainerProperties().setSyncCommits(true);
        factory.setConsumerFactory(this.buildConsumerFactory(OrderEvent.class, props));
        factory.getContainerProperties().setCommitLogLevel(Level.INFO);
        factory.setAckDiscarded(true);
        return factory;
    }

    private ConsumerFactory<String, OrderEvent> buildConsumerFactory(@NonNull Class<OrderEvent> clazz, @NonNull Map<String, Object> extraProps) {
        Map<String, Object> props = new HashMap();
        props.put("bootstrap.servers", bootstrapServer);
        props.put("group.id", applicationName);
        props.putAll(extraProps);
        StringDeserializer keyDeserializer = new StringDeserializer();
        JsonDeserializer<OrderEvent> valueDeserializer = new JsonDeserializer(clazz, this.objectMapper);
        return new DefaultKafkaConsumerFactory(props, keyDeserializer, valueDeserializer);
    }
}
