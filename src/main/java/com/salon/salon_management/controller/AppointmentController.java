
package com.salon.salon_management.controller;

import com.salon.salon_management.entity.Appointment;
import com.salon.salon_management.entity.User;
import com.salon.salon_management.repository.UserRepository;
import com.salon.salon_management.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private com.salon.salon_management.repository.AppointmentRepository appointmentRepository; // Direct repo access for
                                                                                               // simplicity in this
                                                                                               // feature

    @PostMapping
    public Appointment createAppointment(@RequestBody Appointment appointment) {
        // Auto-link User if logged in
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            // Block Admins from booking
            boolean isAdmin = auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ADMIN"));
            if (isAdmin) {
                throw new RuntimeException("Administrators cannot book services.");
            }

            Optional<User> userOpt = userRepository.findByUsername(auth.getName());
            userOpt.ifPresent(appointment::setUser);
        }
        return appointmentService.createAppointment(appointment);
    }

    @GetMapping("/my-appointments")
    public List<Appointment> getMyAppointments() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {

            // Check for Admin Role
            boolean isAdmin = auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ADMIN"));

            if (isAdmin) {
                return appointmentRepository.findByStatus("Pending");
            }

            Optional<User> userOpt = userRepository.findByUsername(auth.getName());
            if (userOpt.isPresent()) {
                return appointmentRepository.findByUser(userOpt.get());
            }
        }
        return List.of(); // Return empty if not logged in
    }

    @GetMapping
    public List<Appointment> getAllAppointments() {
        return appointmentService.getAllAppointments();
    }

    @PutMapping("/{id}/status")
    public Appointment updateStatus(@PathVariable Long id, @RequestBody java.util.Map<String, String> payload) {
        String status = payload.get("status");
        String reason = payload.get("reason");
        return appointmentService.updateStatus(id, status, reason);
    }

    // Better simplified version:
    @PutMapping("/{id}")
    public Appointment updateAppointment(@PathVariable Long id, @RequestBody Appointment details) {
        if (details.getStatus() != null) {
            return appointmentService.updateStatus(id, details.getStatus(), null);
        }
        return null;
    }
}
