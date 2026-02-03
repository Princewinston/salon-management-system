package com.salon.salon_management.repository;

import com.salon.salon_management.entity.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {
    java.util.List<Supplier> findBySupplierName(String supplierName);
}
