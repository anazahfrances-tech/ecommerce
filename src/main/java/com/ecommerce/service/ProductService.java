package com.ecommerce.service;

import com.ecommerce.model.Category;
import com.ecommerce.model.Product;
import java.util.List;
import java.util.Optional;

public interface ProductService {
    Product saveProduct(Product product);
    List<Product> getAllProducts();
    Optional<Product> getProductById(Long id);
    Product updateProduct(Long id, Product productDetails);
    void deleteProduct(Long id);
    void deleteAllProducts();
    List<Product> getProductsByCategory(Category category);
    List<Product> createMultipleProducts(List<Product> products);
    List<Product> searchProductsByName(String name);
}