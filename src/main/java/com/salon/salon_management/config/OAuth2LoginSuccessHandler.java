package com.salon.salon_management.config;

import com.salon.salon_management.entity.Role;
import com.salon.salon_management.entity.User;
import com.salon.salon_management.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

@Component
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Autowired
    private UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {

        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
        String email = oauth2User.getAttribute("email");
        String name = oauth2User.getAttribute("name");

        // Strategy: Use email as username for OAuth users if username not provided
        // Or generate one. For simplicitly, let's look up by email.

        if (email != null) {
            Optional<User> existingUser = userRepository.findByEmail(email);

            if (existingUser.isEmpty()) {
                // Register new user
                User newUser = new User();
                newUser.setEmail(email);

                // Set Username to Name (from Google) or part of email
                String displayName = (name != null) ? name : email.split("@")[0];
                newUser.setUsername(displayName);

                // Role Assignment
                if ("princewinstonp@gmail.com".equalsIgnoreCase(email)) {
                    newUser.setRole(Role.ADMIN);
                } else {
                    newUser.setRole(Role.USER);
                }

                // No password needed for OAuth users
                newUser.setPassword("");

                // Save
                userRepository.save(newUser);
            } else {
                // Update implementation: If user exists, ensure they have ADMIN role if it's
                // the admin email
                // This fixes the issue where the user might have been created as USER first
                User user = existingUser.get();
                if ("princewinstonp@gmail.com".equalsIgnoreCase(email) && user.getRole() != Role.ADMIN) {
                    user.setRole(Role.ADMIN);
                    userRepository.save(user);
                }
            }
        }

        // --- CRITICAL FIX: Update Session Authorities ---
        // Even if we updated the DB, the current session only has OAuth scopes.
        // We must manually update the SecurityContext with the new Role.
        if (email != null) {
            Optional<User> freshUser = userRepository.findByEmail(email);
            if (freshUser.isPresent()) {
                Role role = freshUser.get().getRole(); // ADMIN or USER
                String roleName = "ROLE_" + role.name();

                // Create new authorities list
                java.util.List<org.springframework.security.core.GrantedAuthority> updatedAuthorities = new java.util.ArrayList<>(
                        oauth2User.getAuthorities());
                updatedAuthorities
                        .add(new org.springframework.security.core.authority.SimpleGrantedAuthority(roleName));

                // Create new Auth Token
                org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken newAuth = new org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken(
                        oauth2User,
                        updatedAuthorities,
                        ((org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken) authentication)
                                .getAuthorizedClientRegistrationId());

                // Set into Context
                org.springframework.security.core.context.SecurityContextHolder.getContext().setAuthentication(newAuth);
            }
        }

        // Redirect to dashboard or home
        super.setDefaultTargetUrl("/index.html");
        super.onAuthenticationSuccess(request, response, authentication);
    }
}
