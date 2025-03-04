package br.com.ambevtech.order.data.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {

    private String id;
    private String orderNumber;
    private String status;
    private BigDecimal totalValue;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
    private List<ProductDTO> products;
}
