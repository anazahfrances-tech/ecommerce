package com.ecommerce.service;

import com.ecommerce.model.Order;
import com.ecommerce.model.OrderStatus;
import com.ecommerce.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class PaymentService {

    @Value("${paystack.secret.key}")
    private String secretKey;

    private final OrderRepository orderRepository;
    private final RestTemplate restTemplate;

    public PaymentService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
        this.restTemplate = new RestTemplate();
    }

    // Initialize payment - generates a payment link
    public Map initializePayment(Long orderId, String email) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));

        // Paystack expects amount in pesewas (multiply by 100)
        int amount = (int) (order.getTotalAmount() * 100);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(secretKey);

        Map<String, Object> body = new HashMap<>();
        body.put("email", email);
        body.put("amount", amount);
        body.put("reference", "ORDER_" + orderId + "_" + System.currentTimeMillis());
        body.put("currency", "GHS");

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(
                "https://api.paystack.co/transaction/initialize",
                request,
                Map.class
        );

        return response.getBody();
    }

    // Verify payment - confirm payment was successful
    public Map verifyPayment(String reference, Long orderId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(secretKey);

        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                "https://api.paystack.co/transaction/verify/" + reference,
                HttpMethod.GET,
                request,
                Map.class
        );

        Map responseBody = response.getBody();
        Map data = (Map) responseBody.get("data");
        String status = (String) data.get("status");

        // If payment is successful update order status to CONFIRMED
        if ("success".equals(status)) {
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));
            order.setStatus(OrderStatus.CONFIRMED);
            orderRepository.save(order);
        }

        return responseBody;
    }
}