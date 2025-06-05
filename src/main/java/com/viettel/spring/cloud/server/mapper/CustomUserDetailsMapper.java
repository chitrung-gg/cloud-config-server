package com.viettel.spring.cloud.server.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.viettel.spring.cloud.server.entity.UserEntity;
import com.viettel.spring.cloud.server.security.CustomUserDetails;

@Mapper(componentModel = "spring")
public interface CustomUserDetailsMapper {
    @Mapping(target = "authorities", ignore = true)
    CustomUserDetails convertEntityToCustomDetails(UserEntity userEntity);

    @Mapping(target = "email", ignore = true)
    @Mapping(target = "fullName", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    UserEntity convertCustomDetailsToEntity(CustomUserDetails customUserDetails);
}
