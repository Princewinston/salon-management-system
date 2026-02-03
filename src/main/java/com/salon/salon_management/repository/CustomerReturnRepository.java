package com.salon.salon_management.repository;

import com.salon.salon_management.entity.CustomerReturn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerReturnRepository extends JpaRepository<CustomerReturn, Long> {
}
