package com.viettel.spring.cloud.server.mapper;

import org.mapstruct.Mapper;

import com.viettel.spring.cloud.server.entity.UserEntity;
import com.viettel.spring.cloud.server.security.CustomUserDetails;

@Mapper(componentModel = "spring")
public interface CustomUserDetailsMapper {
    CustomUserDetails convertEntityToCustomDetails(UserEntity userEntity);
}
