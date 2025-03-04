package br.com.ambevtech.order.api.model;

import java.time.LocalDate;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderFilter {

    private String name;
    private String sku;
    private LocalDate startDate;
    private LocalDate endDate;
    private String orderBy;
    private int pageNumber;
    private int pageSize;
    private String direction;
}
