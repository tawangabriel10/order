package br.com.ambevtech.order.api.rest;

import br.com.ambevtech.order.api.model.OrderFilter;
import br.com.ambevtech.order.data.model.OrderDTO;
import br.com.ambevtech.order.service.OrderService;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/orders")
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    public ResponseEntity<List<OrderDTO>> findAllByParams(
        @RequestParam(value = "name", required = false) String name,
        @RequestParam(value = "sku", required = false) String sku,
        @RequestParam(value = "startDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
        @RequestParam(value = "endDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
        @RequestParam(value = "oderBy", required = false, defaultValue = "name") String orderBy,
        @RequestParam(value = "direction", required = false, defaultValue = "ASC") String direction,
        @RequestParam(value = "pageNumber", defaultValue = "0") Integer pageNumber,
        @RequestParam(value = "pageSize", defaultValue = "50") Integer pageSize) {
        final OrderFilter filter = OrderFilter.builder()
            .name(name)
            .sku(sku)
            .startDate(startDate)
            .endDate(endDate)
            .orderBy(orderBy)
            .direction(direction)
            .pageNumber(pageNumber)
            .pageSize(pageSize)
            .build();
        final List<OrderDTO> ordersPaginated = orderService.findAllByFilters(filter);
        return ResponseEntity.ok().body(ordersPaginated);
    }

    @GetMapping(value = "/{orderNumber}")
    public ResponseEntity<OrderDTO> findByOrderNumber(@PathVariable String orderNumber) {
        final OrderDTO orderDTO = orderService.findByOrderNumber(orderNumber);
        return ResponseEntity.ok().body(orderDTO);
    }
}
