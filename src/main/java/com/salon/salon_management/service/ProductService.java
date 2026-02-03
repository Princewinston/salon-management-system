package com.salon.salon_management.service;

import com.salon.salon_management.entity.Product;
import com.salon.salon_management.repository.CategoryRepository;
import com.salon.salon_management.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    public Product createProduct(Product product) {
        if (product.getCategory() != null) {
            String catName = product.getCategory().getCategoryName();
            if (catName != null && !catName.isEmpty()) {
                List<com.salon.salon_management.entity.Category> existingCats = categoryRepository
                        .findByCategoryName(catName);
                if (!existingCats.isEmpty()) {
                    product.setCategory(existingCats.get(0));
                } else {
                    // Save new category if it doesn't exist
                    categoryRepository.save(product.getCategory());
                }
            }
        }
        return productRepository.save(product);
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    public Product updateProduct(Long id, Product productDetails) {
        return productRepository.findById(id).map(product -> {
            product.setProductName(productDetails.getProductName());
            product.setDescription(productDetails.getDescription());
            product.setPrice(productDetails.getPrice());
            product.setStockQuantity(productDetails.getStockQuantity());

            // Handle Category Update
            if (productDetails.getCategory() != null) {
                String catName = productDetails.getCategory().getCategoryName();
                List<com.salon.salon_management.entity.Category> existingCats = categoryRepository
                        .findByCategoryName(catName);
                if (!existingCats.isEmpty()) {
                    product.setCategory(existingCats.get(0));
                } else {
                    categoryRepository.save(productDetails.getCategory());
                    product.setCategory(productDetails.getCategory());
                }
            }

            return productRepository.save(product);
        }).orElse(null);
    }

    public void updateStock(Long productId, Integer quantityToAdd) {
        productRepository.findById(productId).ifPresent(product -> {
            int newStock = (product.getStockQuantity() == null ? 0 : product.getStockQuantity()) + quantityToAdd;
            product.setStockQuantity(newStock);
            productRepository.save(product);
        });
    }
}
