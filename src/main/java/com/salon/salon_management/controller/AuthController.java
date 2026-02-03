package com.salon.salon_management.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

import java.util.stream.Collectors;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.beans.factory.annotation.Autowired;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @GetMapping("/me")
    public Map<String, Object> getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Map<String, Object> response = new HashMap<>();

        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            response.put("authenticated", true);

            // Default roles from session (usually just scopes for OAuth)
            // But we want the DB role (e.g., ROLE_ADMIN)

            // Check if principal is OAuth2
            if (auth.getPrincipal() instanceof org.springframework.security.oauth2.core.user.OAuth2User) {
                org.springframework.security.oauth2.core.user.OAuth2User oauthUser = (org.springframework.security.oauth2.core.user.OAuth2User) auth
                        .getPrincipal();
                String email = oauthUser.getAttribute("email");

                // Fetch from DB to get actual Role
                if (email != null) {
                    com.salon.salon_management.entity.User dbUser = userService.findByEmail(email).orElse(null);
                    if (dbUser != null) {
                        response.put("username", dbUser.getUsername()); // Use DB username preference
                        // Prepend ROLE_ to match Spring Security and frontend expectation
                        response.put("roles", java.util.Collections.singletonList("ROLE_" + dbUser.getRole().name()));
                        return response; // Return early
                    }
                }
            }

            // Fallback for Form Login or if DB lookup failed
            response.put("username", auth.getName());
            // Convert authorities to simple string list [ROLE_ADMIN, ROLE_USER]
            response.put("roles", auth.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList()));
        } else {
            response.put("authenticated", false);
        }

        return response;
    }

    @Autowired
    private com.salon.salon_management.service.UserService userService;

    @org.springframework.web.bind.annotation.PostMapping("/register")
    public org.springframework.http.ResponseEntity<?> registerUser(
            @org.springframework.web.bind.annotation.RequestBody com.salon.salon_management.entity.User user) {
        try {
            com.salon.salon_management.entity.User registeredUser = userService.registerUser(user);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "User registered successfully");
            response.put("userId", registeredUser.getUserId());
            return org.springframework.http.ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return org.springframework.http.ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
