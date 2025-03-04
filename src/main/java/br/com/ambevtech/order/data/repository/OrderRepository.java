package br.com.ambevtech.order.data.repository;

import br.com.ambevtech.order.data.model.OrderDocument;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends MongoRepository<OrderDocument, String> {

    @Query(value = "{ 'orderNumber': ?0 }")
    Optional<OrderDocument> findByOrderNumber(String orderNumber);

}
