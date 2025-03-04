package br.com.ambevtech.order.service;

import static io.micrometer.common.util.StringUtils.isNotBlank;
import static java.util.Objects.nonNull;

import br.com.ambevtech.order.api.model.OrderEvent;
import br.com.ambevtech.order.api.model.OrderFilter;
import br.com.ambevtech.order.config.exception.BusinessException;
import br.com.ambevtech.order.data.model.OrderDTO;
import br.com.ambevtech.order.data.model.OrderDocument;
import br.com.ambevtech.order.data.model.OrderDocument.OrderStatus;
import br.com.ambevtech.order.data.repository.OrderRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository repository;
    private final ModelMapper mapper;
    private final MongoTemplate mongoTemplate;

    /**
     * Method to find all Orders filtering by params to Produto Externo B
     * @param filter
     * @return List Order DTO
     */
    public List<OrderDTO> findAllByFilters(@NonNull OrderFilter filter) {
        log.info("Finding all orders by params");

        final List<Criteria> criterias = new ArrayList<>();
        if (isNotBlank(filter.getName())) {
            criterias.add(Criteria.where("products.name").regex(filter.getName(), "i"));
        }
        if (isNotBlank(filter.getSku())) {
            criterias.add(Criteria.where("products.sku").is(filter.getSku()));
        }
        if (nonNull(filter.getStartDate()) && nonNull(filter.getEndDate())) {
            criterias.add(Criteria.where("createAt").gte(filter.getStartDate().atTime(0, 0, 0))
                .lte(filter.getEndDate().atTime(23, 59, 59)));
        }
        final Criteria criteria = new Criteria();
        if (!criterias.isEmpty()) {
            criteria.andOperator(criterias);
        }

        final Pageable page = PageRequest.of(filter.getPageNumber(), filter.getPageSize());

        Query query = new Query()
            .addCriteria(criteria)
            .with(page)
            .with(Sort.by(Sort.Direction.fromString(filter.getDirection()), filter.getOrderBy()));

        return mongoTemplate.find(query, OrderDocument.class)
            .stream()
            .map(order -> mapper.map(order, OrderDTO.class))
            .toList();
    }

    /**
     * Method to save new Order on data base by Produto Externo A
     * @param orderEvent
     * @return Order DTO
     */
    public OrderDTO save(@NonNull OrderEvent orderEvent) {
        Optional<OrderDocument> opOrder = repository.findByOrderNumber(orderEvent.getOrderNumber());
        if (opOrder.isPresent()) {
            throw new BusinessException("Order Number already exists");
        }

        final OrderDocument order = mapper.map(orderEvent, OrderDocument.class);
        order.setCreateAt(LocalDateTime.now());
        order.setStatus(OrderStatus.STARTED);
        BigDecimal totalValue = order.getProducts().stream()
            .map(product -> {
                if (nonNull(product.getValue()) && nonNull(product.getQuantity())) {
                    return product.getValue().multiply(BigDecimal.valueOf(product.getQuantity()));
                }
                return BigDecimal.ZERO;
            })
            .reduce(BigDecimal.ZERO, BigDecimal::add);;
        order.setTotalValue(totalValue);
        final OrderDocument orderSaved = repository.save(order);
        return mapper.map(orderSaved, OrderDTO.class);
    }

    /**
     * Method to find Order by order number to Produto Externo B
     * @param orderNumber
     * @return Order DTO
     */
    public OrderDTO findByOrderNumber(@NonNull String orderNumber) {
        log.info("Finding an order by orderNumber: {}", orderNumber);
        return repository.findByOrderNumber(orderNumber)
            .map(order -> mapper.map(order, OrderDTO.class))
            .orElseThrow(() -> new BusinessException("Order not found"));
    }

}
