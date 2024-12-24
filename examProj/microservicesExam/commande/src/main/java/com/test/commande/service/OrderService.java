package com.test.commande.service;

import com.test.commande.client.ProductClient;
import com.test.commande.model.Order;
import com.test.commande.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductClient productClient;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    public OrderService(OrderRepository orderRepository,
                        ProductClient productClient,
                        KafkaTemplate<String, String> kafkaTemplate) {
        this.orderRepository = orderRepository;
        this.productClient = productClient;
        this.kafkaTemplate = kafkaTemplate;
    }

    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    public Order findById(Long id) {
        return orderRepository.findById(id).orElse(null);
    }

    public Order createOrder(Long productId, int quantity) {
        // 1) Call Produits via GraphQL to get the price
        Double price = productClient.getProductPrice(productId);

        // 2) Calculate total
        double total = price * quantity;

        // 3) Create the Order in our DB
        Order order = new Order(productId, quantity, total);
        Order saved = orderRepository.save(order);

        // 4) Publish an event to Kafka
        String eventPayload = String.format(
                "{\"event\":\"OrderCreated\",\"orderId\":%d,\"productId\":%d,\"quantity\":%d,\"totalPrice\":%.2f}",
                saved.getId(), saved.getProductId(), saved.getQuantity(), saved.getTotalPrice()
        );
        // We assume a topic named "orders"
        kafkaTemplate.send("orders", eventPayload);

        return saved;
    }

    public Order updateOrder(Long id, Order newData) {
        return orderRepository.findById(id)
                .map(existing -> {
                    existing.setProductId(newData.getProductId());
                    existing.setQuantity(newData.getQuantity());
                    existing.setTotalPrice(newData.getTotalPrice());
                    return orderRepository.save(existing);
                })
                .orElse(null);
    }

    public boolean deleteOrder(Long id) {
        if (orderRepository.existsById(id)) {
            orderRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
