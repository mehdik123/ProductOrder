package com.test.notification.consumer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.test.notification.model.Notification;
import com.test.notification.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class NotificationListener {

    @Autowired
    private NotificationService notificationService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @KafkaListener(topics = "orders", groupId = "notifications-group")
    public void onOrderCreated(String message) {
        // Example of a received message:
        // {
        //   "event":"OrderCreated",
        //   "orderId":123,
        //   "productId":10,
        //   "quantity":2,
        //   "totalPrice": 299.99
        // }

        try {
            JsonNode root = objectMapper.readTree(message);
            String event = root.path("event").asText();

            if ("OrderCreated".equals(event)) {
                long orderId = root.path("orderId").asLong();
                long productId = root.path("productId").asLong();
                int qty = root.path("quantity").asInt();
                double total = root.path("totalPrice").asDouble();

                // Build a custom message
                String msg = String.format(
                        "A new order (ID=%d) was created for product=%d (qty=%d) total=%.2f",
                        orderId, productId, qty, total
                );

                // Create and save a Notification
                Notification notif = new Notification(msg, "someUser@example.com", false);
                notificationService.create(notif);

                System.out.println("[NOTIFICATION] Created: " + msg);
            }

        } catch (Exception e) {
            System.err.println("[NOTIFICATION] Error parsing message: " + e.getMessage());
        }
    }
}
