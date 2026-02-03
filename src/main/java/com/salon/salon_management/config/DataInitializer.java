package com.salon.salon_management.config;

import com.salon.salon_management.entity.Category;
import com.salon.salon_management.entity.Product;
import com.salon.salon_management.entity.Role;
import com.salon.salon_management.entity.User;
import com.salon.salon_management.repository.CategoryRepository;
import com.salon.salon_management.repository.ProductRepository;
import com.salon.salon_management.repository.UserRepository;
import java.util.Arrays;
import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

        private final ProductRepository productRepository;
        private final CategoryRepository categoryRepository;
        private final com.salon.salon_management.repository.SupplierRepository supplierRepository;
        private final UserRepository userRepository;
        private final PasswordEncoder passwordEncoder;

        public DataInitializer(ProductRepository productRepository, CategoryRepository categoryRepository,
                        com.salon.salon_management.repository.SupplierRepository supplierRepository,
                        UserRepository userRepository, PasswordEncoder passwordEncoder) {
                this.productRepository = productRepository;
                this.categoryRepository = categoryRepository;
                this.supplierRepository = supplierRepository;
                this.userRepository = userRepository;
                this.passwordEncoder = passwordEncoder;
        }

        @Override
        public void run(String... args) throws Exception {
                // 0. Rename "Nail" to "Nails" if exists
                List<Category> nailCats = categoryRepository.findByCategoryName("Nail");
                if (!nailCats.isEmpty()) {
                        for (Category cat : nailCats) {
                                cat.setCategoryName("Nails");
                                categoryRepository.save(cat);
                        }
                        System.out.println("Renamed " + nailCats.size() + " 'Nail' categories to 'Nails'.");
                }

                // 1. Seed Requested Categories
                List<String> categories = Arrays.asList("Hair", "Skin", "Nails", "Spa", "Special");
                for (String catName : categories) {
                        List<Category> existing = categoryRepository.findByCategoryName(catName);
                        if (existing.isEmpty()) {
                                categoryRepository.save(new Category(catName));
                                System.out.println("Seeded Category: " + catName);
                        }
                }

                // 2. Remove Duplicate "Velvet Touch Manicure"
                String duplicateTarget = "Velvet Touch Manicure";
                List<Product> duplicates = productRepository.findByProductName(duplicateTarget);
                if (duplicates.size() > 1) {
                        // Keep the first one, delete the rest
                        System.out.println("Found " + duplicates.size() + " instances of " + duplicateTarget
                                        + ". Removing duplicates...");
                        for (int i = 1; i < duplicates.size(); i++) {
                                productRepository.delete(duplicates.get(i));
                        }
                        System.out.println("Duplicates removed.");
                }

                System.out.println("Duplicates removed.");

                // 3. Remove Duplicate Suppliers (Generic check)
                java.util.List<com.salon.salon_management.entity.Supplier> allSuppliers = supplierRepository.findAll();
                java.util.Map<String, java.util.List<com.salon.salon_management.entity.Supplier>> suppliersByName = allSuppliers
                                .stream()
                                .collect(java.util.stream.Collectors.groupingBy(
                                                com.salon.salon_management.entity.Supplier::getSupplierName));

                for (java.util.Map.Entry<String, java.util.List<com.salon.salon_management.entity.Supplier>> entry : suppliersByName
                                .entrySet()) {
                        if (entry.getValue().size() > 1) {
                                System.out.println("Found duplicate suppliers for: " + entry.getKey());
                                // Skip the first one, delete others
                                for (int i = 1; i < entry.getValue().size(); i++) {
                                        supplierRepository.delete(entry.getValue().get(i));
                                }
                        }
                }

                System.out.println("Database check complete. New categories added if missing.");

                // 4. Seed Users
                if (userRepository.findByUsername("admin").isEmpty()) {
                        User admin = new User();
                        admin.setUsername("admin");
                        admin.setEmail("admin@salon.com");
                        admin.setPassword(passwordEncoder.encode("admin123"));
                        admin.setRole(Role.ADMIN);
                        userRepository.save(admin);
                        System.out.println("Default admin user created: admin / admin123");
                }

                if (userRepository.findByUsername("user").isEmpty()) {
                        User user = new User();
                        user.setUsername("user");
                        user.setEmail("user@salon.com");
                        user.setPassword(passwordEncoder.encode("user123"));
                        user.setRole(Role.USER);
                        userRepository.save(user);
                        System.out.println("Default standard user created: user / user123");
                }
        }
}
