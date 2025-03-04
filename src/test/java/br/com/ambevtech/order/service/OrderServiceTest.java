package br.com.ambevtech.order.service;

import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import br.com.ambevtech.order.Fixture;
import br.com.ambevtech.order.api.model.OrderEvent;
import br.com.ambevtech.order.api.model.OrderFilter;
import br.com.ambevtech.order.config.exception.BusinessException;
import br.com.ambevtech.order.data.model.OrderDTO;
import br.com.ambevtech.order.data.model.OrderDocument;
import br.com.ambevtech.order.data.repository.OrderRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.mongodb.core.MongoTemplate;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @InjectMocks private OrderService orderService;
    @Mock private OrderRepository repository;
    @Mock private ModelMapper mapper;
    @Mock private MongoTemplate mongoTemplate;

    @Test
    void givenOrderFilter_whenCallFindAllByFilters_thenReturnListOrdersDTO() {
        final OrderFilter orderFilter = OrderFilter.builder()
            .pageNumber(1)
            .pageSize(10)
            .orderBy("orderNumber")
            .direction("ASC")
            .build();
        final OrderDocument orderDocument = Fixture.make(new OrderDocument());
        final OrderDTO orderDTO = Fixture.make(OrderDTO.builder().build());

        when(mongoTemplate.find(any(), eq(OrderDocument.class))).thenReturn(singletonList(orderDocument));
        when(mapper.map(any(), eq(OrderDTO.class))).thenReturn(orderDTO);

        List<OrderDTO> orderDTOS = orderService.findAllByFilters(orderFilter);
        assertNotNull(orderDTOS);
        assertEquals(1, orderDTOS.size());
        assertEquals(orderDTO.getOrderNumber(), orderDTOS.get(0).getOrderNumber());

        verify(mongoTemplate).find(any(), eq(OrderDocument.class));
        verify(mapper).map(orderDocument, OrderDTO.class);
    }

    @Test
    void givenOrderFilterNull_whenCallFindAllByFilters_thenThrowsNullPointerException() {
        assertThrows(NullPointerException.class, () -> orderService.findAllByFilters(null));
        verifyNoInteractions(mongoTemplate, mapper);
    }

    @Test
    void givenOrderEvent_whenCallSave_thenSaveOrderAndReturnDTO() {
        final OrderDocument orderDocument = Fixture.make(new OrderDocument());
        final OrderEvent orderEvent = Fixture.make(OrderEvent.builder().build());
        final OrderDTO orderDTO = Fixture.make(OrderDTO.builder().build());

        when(repository.findByOrderNumber(any())).thenReturn(Optional.empty());
        when(mapper.map(any(), eq(OrderDocument.class))).thenReturn(orderDocument);
        when(mapper.map(any(), eq(OrderDTO.class))).thenReturn(orderDTO);

        final OrderDTO response = orderService.save(orderEvent);
        assertNotNull(response);

        verify(repository).findByOrderNumber(orderEvent.getOrderNumber());
        verify(mapper).map(any(), eq(OrderDocument.class));
        verify(mapper).map(any(), eq(OrderDTO.class));
    }

    @Test
    void givenOrderEvent_whenCallSaveAndFindOrderByOderNumber_thenThrowsBusinessException() {
        final OrderDocument orderDocument = Fixture.make(new OrderDocument());
        final OrderEvent orderEvent = Fixture.make(OrderEvent.builder().build());

        when(repository.findByOrderNumber(any())).thenReturn(Optional.of(orderDocument));
        assertThrows(BusinessException.class, () -> orderService.save(orderEvent));

        verify(repository).findByOrderNumber(orderEvent.getOrderNumber());
        verifyNoInteractions(mapper);
    }

    @Test
    void givenOrderEventNull_whenCallSave_thenThrowsNullPointerException() {
        assertThrows(NullPointerException.class, () -> orderService.save(null));
        verifyNoInteractions(repository, mapper);
    }

    @Test
    void givenOrderNumber_whenCallFindByOrderNumber_thenReturnOrderDTO() {
        final String orderNumber = "0242ac120002";
        final OrderDocument orderDocument = Fixture.make(new OrderDocument());
        final OrderDTO orderDTO = Fixture.make(OrderDTO.builder().build());

        when(repository.findByOrderNumber(any())).thenReturn(Optional.of(orderDocument));
        when(mapper.map(any(), eq(OrderDTO.class))).thenReturn(orderDTO);

        final OrderDTO response = orderService.findByOrderNumber(orderNumber);
        assertNotNull(response);
        assertEquals(orderDTO.getOrderNumber(), response.getOrderNumber());

        verify(repository).findByOrderNumber(orderNumber);
        verify(mapper).map(orderDocument, OrderDTO.class);
    }

    @Test
    void givenOrderNumberNull_whenCallFindByOrderNumber_thenThrowsNullPointerException() {
        assertThrows(NullPointerException.class, () -> orderService.findByOrderNumber(null));
        verifyNoInteractions(repository, mapper);
    }
}
