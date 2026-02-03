package com.salon.salon_management.repository;

import com.salon.salon_management.entity.Appointment;
import com.salon.salon_management.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByAppointmentDate(LocalDate date);

    List<Appointment> findByUser(User user);

    List<Appointment> findByStatus(String status);
}
