package br.com.ambevtech.order.data.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.GeneratedValue;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@Document(collection = "orders")
public class OrderDocument {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid4")
    private ObjectId id;
    private String orderNumber;
    private OrderStatus status;
    private BigDecimal totalValue;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
    private List<Product> products;
    @Version
    private Long version;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Product {
        private String name;
        private String sku;
        private Integer quantity;
        private BigDecimal value;

    }

    public enum OrderStatus {
        STARTED, IN_PROGRESS, FINISHED;
    }

}
