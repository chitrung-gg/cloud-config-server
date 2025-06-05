package com.viettel.spring.cloud.server.config.security;

import java.io.Serializable;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.viettel.spring.cloud.server.dto.userpermission.UserPermissionDto;
import com.viettel.spring.cloud.server.security.CustomUserDetails;
import com.viettel.spring.cloud.server.service.UserPermissionService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserPermissionEvaluator implements PermissionEvaluator {
    @Autowired
    private final UserPermissionService userPermissionService;

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType,
            Object permission) {
        System.out.println(targetId);
        return true;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        if (authentication == null || permission == null) {
            return false;
        }
        
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        System.out.println(targetDomainObject);
        if (userDetails == null) {
            return false;
        }

        String permissionStr = permission.toString();

        // Call PermissionService to get user permissions
        List<UserPermissionDto> userPermissions = userPermissionService.findUserPermissionByUserId(userDetails.getId());

        // Check if user has the required permission
        return userPermissions.stream()
                .anyMatch(up -> up.getPermission().name().equalsIgnoreCase(permissionStr));
    }

    

    // private Long extractUserIdFromAuthentication(Authentication authentication) {
    //     // Adjust this method based on your actual UserDetails implementation
    //     Object principal = authentication.getPrincipal();
    //     if (principal instanceof org.springframework.security.core.userdetails.UserDetails) {
    //         // If you have a custom UserDetails with getUserId method, cast and call it
    //         // For now, return null or implement based on your UserDetails structure
    //        return ((CustomUserDetails) principal).getId();
    //     }
    //     return null;
    // }

}
