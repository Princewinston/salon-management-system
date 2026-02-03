package com.salon.salon_management.dto;

import java.util.List;
import java.util.Map;

public class AnalyticsDTO {
    private List<String> revenueLabels; // e.g. Mon, Tue
    private List<Double> revenueData; // e.g. 150.0, 200.0
    private List<String> popularityLabels; // e.g. Hair, Nails
    private List<Integer> popularityData; // e.g. 10, 5

    // Getters and Setters
    public List<String> getRevenueLabels() {
        return revenueLabels;
    }

    public void setRevenueLabels(List<String> revenueLabels) {
        this.revenueLabels = revenueLabels;
    }

    public List<Double> getRevenueData() {
        return revenueData;
    }

    public void setRevenueData(List<Double> revenueData) {
        this.revenueData = revenueData;
    }

    public List<String> getPopularityLabels() {
        return popularityLabels;
    }

    public void setPopularityLabels(List<String> popularityLabels) {
        this.popularityLabels = popularityLabels;
    }

    public List<Integer> getPopularityData() {
        return popularityData;
    }

    public void setPopularityData(List<Integer> popularityData) {
        this.popularityData = popularityData;
    }
}
