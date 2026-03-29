package com.ecommerce.controller;

import com.ecommerce.model.Cart;
import com.ecommerce.model.Order;
import com.ecommerce.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    // View cart
    @GetMapping("/{userId}")
    public Cart getCart(@PathVariable Long userId) {
        return cartService.getCart(userId);
    }

    // Add item
    @PostMapping("/{userId}/add")
    public Cart addItem(
            @PathVariable Long userId,
            @RequestParam Long productId,
            @RequestParam int quantity) {
        return cartService.addItem(userId, productId, quantity);
    }

    // Remove item
    @DeleteMapping("/{userId}/remove/{cartItemId}")
    public Cart removeItem(@PathVariable Long userId, @PathVariable Long cartItemId) {
        return cartService.removeItem(userId, cartItemId);
    }

    // Update quantity
    @PutMapping("/{userId}/update/{cartItemId}")
    public Cart updateQuantity(
            @PathVariable Long userId,
            @PathVariable Long cartItemId,
            @RequestParam int quantity) {
        return cartService.updateItemQuantity(userId, cartItemId, quantity);
    }

    // Clear cart
    @DeleteMapping("/{userId}/clear")
    public Cart clearCart(@PathVariable Long userId) {
        return cartService.clearCart(userId);
    }

    // Checkout
    @PostMapping("/{userId}/checkout")
    public Order checkout(@PathVariable Long userId) {
        return cartService.checkout(userId);
    }
}
