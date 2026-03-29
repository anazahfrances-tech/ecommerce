package com.ecommerce.service;

import com.ecommerce.model.Product;
import com.ecommerce.model.Category;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    // Save a single product
    @Override
    public Product saveProduct(Product product) {
        // Fetch full category from DB to avoid null name
        Category category = categoryRepository.findById(
                product.getCategory().getId()
        ).orElseThrow(() -> new RuntimeException("Category not found"));
        product.setCategory(category);

        return productRepository.save(product);
    }

    // Save multiple products
    @Override
    public List<Product> createMultipleProducts(List<Product> products) {
        for (Product product : products) {
            Category category = categoryRepository.findById(
                    product.getCategory().getId()
            ).orElseThrow(() -> new RuntimeException("Category not found"));
            product.setCategory(category);
        }
        return productRepository.saveAll(products);
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    @Override
    public List<Product> getProductsByCategory(Category category) {
        return productRepository.findByCategory(category);
    }

    @Override
    public List<Product> searchProductsByName(String name) {
        return productRepository.findByNameContainingIgnoreCase(name);
    }

    // Update product
    @Override
    public Product updateProduct(Long id, Product productDetails) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));

        existingProduct.setName(productDetails.getName());
        existingProduct.setDescription(productDetails.getDescription());
        existingProduct.setPrice(productDetails.getPrice());
        existingProduct.setQuantity(productDetails.getQuantity());

        // Fetch full category from DB to avoid null name
        Category category = categoryRepository.findById(
                productDetails.getCategory().getId()
        ).orElseThrow(() -> new RuntimeException("Category not found"));

        existingProduct.setCategory(category);

        return productRepository.save(existingProduct);
    }

    // Delete product
    @Override
    public void deleteProduct(Long id) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
        productRepository.delete(existingProduct);
    }
        @Override
        public void deleteAllProducts() {
            productRepository.deleteAll();

    }
    }
