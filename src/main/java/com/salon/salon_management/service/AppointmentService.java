package com.salon.salon_management.service;

import com.salon.salon_management.entity.Appointment;
import com.salon.salon_management.repository.AppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    public Appointment createAppointment(Appointment appointment) {
        // Simple conflict check logic can go here (optional)
        appointment.setStatus("Pending");
        return appointmentRepository.save(appointment);
    }

    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }

    public List<Appointment> getAppointmentsByDate(LocalDate date) {
        return appointmentRepository.findByAppointmentDate(date);
    }

    public Appointment updateStatus(Long id, String status, String reason) {
        return appointmentRepository.findById(id).map(appt -> {
            appt.setStatus(status);
            if ("Cancelled".equalsIgnoreCase(status) && reason != null) {
                appt.setCancellationReason(reason);
            }
            return appointmentRepository.save(appt);
        }).orElse(null);
    }
}
