package br.com.ambevtech.order.api.rest;

import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.com.ambevtech.order.Fixture;
import br.com.ambevtech.order.data.model.OrderDTO;
import br.com.ambevtech.order.service.OrderService;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

    @InjectMocks private OrderController controller;
    @Mock private OrderService orderService;

    @Test
    void givenParamsRequest_whenCallFindAllByParams_thenReturnListOrderDTO() {
        final OrderDTO orderDTO = Fixture.make(OrderDTO.builder()
            .build());
        String name = "Água Mineral";
        String sku = "176e03b2-f7c7-11ef-9cd2-0242ac120002";
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now();
        String orderBy = "name";
        String direction = "ASC";
        Integer pageNumber = 1;
        Integer pageSize = 10;

        when(orderService.findAllByFilters(any())).thenReturn(singletonList(orderDTO));

        final ResponseEntity<List<OrderDTO>> response = controller.findAllByParams(name, sku, startDate, endDate,
            orderBy, direction, pageNumber, pageSize);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(orderDTO.getOrderNumber(), response.getBody().get(0).getOrderNumber());

        verify(orderService).findAllByFilters(any());
    }

}
