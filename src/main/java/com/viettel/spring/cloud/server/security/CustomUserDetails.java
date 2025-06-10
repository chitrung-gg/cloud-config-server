package com.viettel.spring.cloud.server.security;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.viettel.spring.cloud.server.entity.UserEntity;
import com.viettel.spring.cloud.server.entity.UserEntity.Role;
import com.viettel.spring.cloud.server.entity.UserPermissionEntity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonTypeInfo(
    use = JsonTypeInfo.Id.CLASS,
    include = JsonTypeInfo.As.PROPERTY,
    property = "@class"
)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CustomUserDetails implements UserDetails 
{    private Long id;
    private String username;
    private String password;
    private Role role;
    
    private Collection<? extends GrantedAuthority> authorities;

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
    
    // @Override 
    // @JsonIgnore
    // public boolean isAccountNonExpired() { 
    //     return true; 
    // }

    // @Override 
    // @JsonIgnore
    // public boolean isAccountNonLocked() { 
    //     return true; 
    // }

    // @Override
    // @JsonIgnore
    // public boolean isCredentialsNonExpired() {
    //     return true;
    // }
    
    // @Override
    // @JsonIgnore
    // public boolean isEnabled() {
    //     return true; 
    // }
}
