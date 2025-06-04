package com.viettel.spring.cloud.server.security;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.viettel.spring.cloud.server.entity.UserEntity;
import com.viettel.spring.cloud.server.entity.UserEntity.Role;
import com.viettel.spring.cloud.server.entity.UserPermissionEntity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CustomUserDetails implements UserDetails 
{
    private final Long id;
    private final String username;
    private final String password;
    private final Role role;
    private final Collection<? extends GrantedAuthority> authorities;

    public static CustomUserDetails fromUserEntity(UserEntity userEntity, List<UserPermissionEntity> userPermissionEntity) {
        List<GrantedAuthority> authorities = userPermissionEntity.stream()
            .map(permissionEntity -> new SimpleGrantedAuthority(permissionEntity.getPermission().name()))
            .collect(Collectors.toList());

        authorities.add(new SimpleGrantedAuthority("ROLE_" + userEntity.getRole().name()));

        return new CustomUserDetails(
            userEntity.getId(),
            userEntity.getUsername(),
            userEntity.getPassword(),
            userEntity.getRole(),
            authorities
        );
    }

    @Override 
    public boolean isAccountNonExpired() { 
        return true; 
    }

    @Override 
    public boolean isAccountNonLocked() { 
        return true; 
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    
    @Override
    public boolean isEnabled() {
        return true; 
    }
}
