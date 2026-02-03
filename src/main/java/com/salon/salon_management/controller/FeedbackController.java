package com.salon.salon_management.controller;

import com.salon.salon_management.entity.Feedback;
import com.salon.salon_management.service.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/feedback")
@CrossOrigin(origins = "*")
public class FeedbackController {

    @Autowired
    private FeedbackService feedbackService;

    @PostMapping
    public Feedback submitFeedback(@RequestBody Feedback feedback) {
        return feedbackService.submitFeedback(feedback);
    }

    @GetMapping
    public List<Feedback> getAllFeedback(@RequestParam(required = false) Long productId) {
        if (productId != null) {
            return feedbackService.getFeedbackByProductId(productId);
        }
        return feedbackService.getAllFeedback();
    }
}
