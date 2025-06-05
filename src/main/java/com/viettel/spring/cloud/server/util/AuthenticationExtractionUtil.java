package com.viettel.spring.cloud.server.util;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.viettel.spring.cloud.server.security.CustomUserDetails;

@Component
public class AuthenticationExtractionUtil {
    public static CustomUserDetails extractUserIdFromAuthentication(Authentication authentication) {
        // Adjust this method based on your actual UserDetails implementation
        Object principal = authentication.getPrincipal();
        if (principal instanceof org.springframework.security.core.userdetails.UserDetails) {
            // If you have a custom UserDetails with getUserId method, cast and call it
            // For now, return null or implement based on your UserDetails structure
           return ((CustomUserDetails) principal);
        }
        return null;
    }
}
