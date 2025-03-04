package br.com.ambevtech.order.api.stream;

import static br.com.ambevtech.order.config.KafkaConfig.CONTAINER_FACTORY_BEAN_NAME;
import static br.com.ambevtech.order.config.KafkaConfig.SAVE_ORDER_TOPIC;

import br.com.ambevtech.order.api.model.OrderEvent;
import br.com.ambevtech.order.config.exception.BusinessException;
import br.com.ambevtech.order.service.OrderService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderConsumer {

    private final OrderService service;

    @KafkaListener(topics = SAVE_ORDER_TOPIC,
        containerFactory = CONTAINER_FACTORY_BEAN_NAME)
    public void consume(@NonNull OrderEvent event, @NonNull Acknowledgment acknowledgment) {
        acknowledgment.acknowledge();

        try {
            log.info("Consuming event on topic SAVE_ORDER_TOPIC value: {}", event);
            service.save(event);
        } catch(BusinessException ex) {
            log.error("Failed to save Order by message: {}", ex.getMessage());
        }
    }

}
