package com.ecommerce.controller;

import com.ecommerce.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    // Initialize payment
    @PostMapping("/initialize/{orderId}")
    public Map initializePayment(
            @PathVariable Long orderId,
            @RequestParam String email) {
        return paymentService.initializePayment(orderId, email);
    }

    // Verify payment
    @GetMapping("/verify/{reference}/{orderId}")
    public Map verifyPayment(
            @PathVariable String reference,
            @PathVariable Long orderId) {
        return paymentService.verifyPayment(reference, orderId);
    }
}