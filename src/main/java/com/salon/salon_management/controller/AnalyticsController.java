package com.salon.salon_management.controller;

import com.salon.salon_management.dto.AnalyticsDTO;
import com.salon.salon_management.entity.Appointment;
import com.salon.salon_management.repository.AppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.DayOfWeek;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @GetMapping
    public AnalyticsDTO getAnalytics() {
        List<Appointment> allAppointments = appointmentRepository.findAll();

        // Filter Confirmed Only
        List<Appointment> confirmedApps = allAppointments.stream()
                .filter(a -> "Confirmed".equalsIgnoreCase(a.getStatus()))
                .collect(Collectors.toList());

        AnalyticsDTO dto = new AnalyticsDTO();

        // 1. Calculate Revenue per Day (Last 7 days simplified to Day of Week bucket)
        // Map<DayOfWeek, Double> revenueMap = new EnumMap<>(DayOfWeek.class);
        // Initialize all days to 0
        Map<String, Double> weeklyRevenue = new LinkedHashMap<>();
        String[] days = { "Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun" };
        for (String d : days)
            weeklyRevenue.put(d, 0.0);

        for (Appointment app : confirmedApps) {
            if (app.getAppointmentDate() != null && app.getService() != null) {
                String dayName = app.getAppointmentDate().getDayOfWeek()
                        .getDisplayName(TextStyle.SHORT, Locale.ENGLISH);

                // Fix for Mon/Tue consistency if needed, but SHORT returns "Mon", "Tue" etc.
                if (weeklyRevenue.containsKey(dayName)) {
                    weeklyRevenue.put(dayName, weeklyRevenue.get(dayName) + app.getService().getPrice());
                }
            }
        }

        dto.setRevenueLabels(new ArrayList<>(weeklyRevenue.keySet()));
        dto.setRevenueData(new ArrayList<>(weeklyRevenue.values()));

        // 2. Calculate Service Popularity (by Category or Name)
        Map<String, Integer> popularityMap = new HashMap<>();

        for (Appointment app : confirmedApps) { // Use all or confirmed? Let's use all for popularity
            if (app.getService() != null) {
                // Try category first, else product name
                String key = "General";
                if (app.getService().getCategory() != null) {
                    key = app.getService().getCategory().getCategoryName();
                } else {
                    key = app.getService().getProductName();
                }

                popularityMap.put(key, popularityMap.getOrDefault(key, 0) + 1);
            }
        }

        dto.setPopularityLabels(new ArrayList<>(popularityMap.keySet()));
        dto.setPopularityData(new ArrayList<>(popularityMap.values()));

        return dto;
    }
}
