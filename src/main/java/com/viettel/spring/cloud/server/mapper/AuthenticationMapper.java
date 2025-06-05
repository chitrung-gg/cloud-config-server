package com.viettel.spring.cloud.server.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.viettel.spring.cloud.server.dto.authentication.AuthenticationRequestDto;
import com.viettel.spring.cloud.server.dto.authentication.AuthenticationResponseDto;
import com.viettel.spring.cloud.server.dto.authentication.RegisterRequestDto;
import com.viettel.spring.cloud.server.entity.UserEntity;

@Mapper(componentModel = "spring")
public interface AuthenticationMapper {
    @Mapping(target = "usernameOrEmail", ignore = true)
    AuthenticationRequestDto convertEntityToRequestDto(UserEntity userEntity);
    
    @Mapping(target = "accessToken", ignore = true)
    @Mapping(target = "expiryTime", ignore = true)
    @Mapping(target = "tokenType", ignore = true)
    AuthenticationResponseDto convertEntityToResponseDto(UserEntity userEntity);
    
    RegisterRequestDto convertEntityToRegisterDto(UserEntity userEntity);
    
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "username", ignore = true)
    @Mapping(target = "fullName", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    UserEntity convertRequestDtoToEntity(AuthenticationRequestDto authenticationRequestDto);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "username", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "fullName", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    UserEntity convertResponseDtoToEntity(AuthenticationResponseDto authenticationRequestDto);
    
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "role", ignore = true)
    UserEntity convertRegisterDtoToEntity(RegisterRequestDto authenticationRequestDto);
}
