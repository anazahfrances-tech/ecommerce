package com.ecommerce.service;

import com.ecommerce.model.*;
import com.ecommerce.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    // Get or create cart for user
    private Cart getOrCreateCart(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        return cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Cart cart = new Cart();
                    cart.setUser(user);
                    return cartRepository.save(cart);
                });
    }

    // View cart
    public Cart getCart(Long userId) {
        return getOrCreateCart(userId);
    }

    // Add item to cart
    public Cart addItem(Long userId, Long productId, int quantity) {
        Cart cart = getOrCreateCart(userId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));

        // If product already in cart, increase quantity
        for (CartItem item : cart.getCartItems()) {
            if (item.getProduct().getId().equals(productId)) {
                item.setQuantity(item.getQuantity() + quantity);
                cartItemRepository.save(item);
                return cartRepository.save(cart);
            }
        }

        // Otherwise add new cart item
        CartItem cartItem = new CartItem();
        cartItem.setProduct(product);
        cartItem.setQuantity(quantity);
        cartItem.setPrice(product.getPrice());
        cartItem.setCart(cart);
        cart.getCartItems().add(cartItem);

        return cartRepository.save(cart);
    }

    // Remove item from cart
    public Cart removeItem(Long userId, Long cartItemId) {
        Cart cart = getOrCreateCart(userId);
        cart.getCartItems().removeIf(item -> item.getId().equals(cartItemId));
        return cartRepository.save(cart);
    }

    // Update item quantity
    public Cart updateItemQuantity(Long userId, Long cartItemId, int quantity) {
        Cart cart = getOrCreateCart(userId);
        for (CartItem item : cart.getCartItems()) {
            if (item.getId().equals(cartItemId)) {
                if (quantity <= 0) {
                    cart.getCartItems().remove(item);
                } else {
                    item.setQuantity(quantity);
                    cartItemRepository.save(item);
                }
                break;
            }
        }
        return cartRepository.save(cart);
    }

    // Clear cart
    public Cart clearCart(Long userId) {
        Cart cart = getOrCreateCart(userId);
        cart.getCartItems().clear();
        return cartRepository.save(cart);
    }

    // Checkout - convert cart to order
    public Order checkout(Long userId) {
        Cart cart = getOrCreateCart(userId);

        if (cart.getCartItems().isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        Order order = new Order();
        order.setUser(cart.getUser());

        List<OrderItem> orderItems = new ArrayList<>();
        double total = 0;

        for (CartItem cartItem : cart.getCartItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(cartItem.getPrice() * cartItem.getQuantity());
            orderItem.setOrder(order);
            orderItems.add(orderItem);
            total += orderItem.getPrice();
        }

        order.setOrderItems(orderItems);
        order.setTotalAmount(total);
        orderRepository.save(order);

        // Clear cart after checkout
        cart.getCartItems().clear();
        cartRepository.save(cart);

        return order;
    }
}