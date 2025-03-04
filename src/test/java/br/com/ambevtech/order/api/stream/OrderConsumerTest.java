package br.com.ambevtech.order.api.stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.com.ambevtech.order.Fixture;
import br.com.ambevtech.order.api.model.OrderEvent;
import br.com.ambevtech.order.data.model.OrderDTO;
import br.com.ambevtech.order.service.OrderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.support.Acknowledgment;

@ExtendWith(MockitoExtension.class)
class OrderConsumerTest {

    @InjectMocks private OrderConsumer consumer;
    @Mock private OrderService service;
    @Mock private Acknowledgment acknowledgment;

    @Test
    void givenOrderEvent_whenCallConsume_thenSaveOrder() {
        final OrderEvent event = Fixture.make(OrderEvent.builder().build());
        final OrderDTO orderDTO = Fixture.make(OrderDTO.builder().build());

        doNothing().when(acknowledgment).acknowledge();
        when(service.save(any())).thenReturn(orderDTO);

        consumer.consume(event, acknowledgment);

        verify(acknowledgment).acknowledge();
        verify(service).save(event);
    }
}
