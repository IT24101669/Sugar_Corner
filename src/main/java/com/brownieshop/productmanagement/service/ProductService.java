// src/main/java/com/brownieshop/productmanagement/service/ProductService.java
package com.brownieshop.productmanagement.service;



import com.brownieshop.productmanagement.model.Product;
import com.brownieshop.productmanagement.Repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository repository;

    public Product createProduct(Product product) {
        normalizeAvailability(product);
        return repository.save(product);
    }

    public Product updateProduct(Long id, Product updated) {
        Product existing = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));

        // Update only provided fields (partial update)
        if (updated.getName() != null)        existing.setName(updated.getName());
        if (updated.getDescription() != null) existing.setDescription(updated.getDescription());
        if (updated.getPrice() != null)       existing.setPrice(updated.getPrice());
        if (updated.getCategory() != null)    existing.setCategory(updated.getCategory());
        if (updated.getImageUrl() != null)    existing.setImageUrl(updated.getImageUrl());
        if (updated.getQuantity() != null)    existing.setQuantity(updated.getQuantity());
        if (updated.getFeatured() != null)    existing.setFeatured(updated.getFeatured());

        normalizeAvailability(existing);

        return repository.save(existing);
    }

    public void deleteProduct(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Product not found with id: " + id);
        }
        repository.deleteById(id);
    }

    public List<Product> getAllProducts() {
        return repository.findAll();
    }

    public Product getProductById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
    }

    public List<Product> searchByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return getAllProducts();
        }
        return repository.findByNameContainingIgnoreCase(name.trim());
    }

    public List<Product> filterProducts(String category, Double minPrice, Double maxPrice) {
        return repository.findFiltered(
                category != null && !category.trim().isEmpty() ? category.trim() : null,
                minPrice,
                maxPrice
        );
    }

    public List<Product> getFeaturedProducts() {
        return repository.findByFeaturedTrue();
    }

    private void normalizeAvailability(Product p) {
        if (p.getQuantity() == null || p.getQuantity() <= 0) {
            p.setQuantity(0);
            p.setAvailable(false);
        } else {
            p.setAvailable(true);
        }
    }
    // ProductService එකේ
    public Product reduceStock(Long productId, Integer quantityToReduce) {
        Product product = getProductById(productId);

        if (product.getQuantity() < quantityToReduce) {
            throw new RuntimeException("Insufficient stock");
        }

        product.setQuantity(product.getQuantity() - quantityToReduce);
        normalizeAvailability(product);

        return repository.save(product);
    }
}