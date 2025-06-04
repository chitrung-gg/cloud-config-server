package com.viettel.spring.cloud.server.mapper;

import org.mapstruct.Mapper;

import com.viettel.spring.cloud.server.dto.authentication.AuthenticationRequestDto;
import com.viettel.spring.cloud.server.dto.authentication.AuthenticationResponseDto;
import com.viettel.spring.cloud.server.dto.authentication.RegisterRequestDto;
import com.viettel.spring.cloud.server.entity.UserEntity;

@Mapper(componentModel = "spring")
public interface AuthenticationMapper {
    AuthenticationRequestDto convertEntityToRequestDto(UserEntity userEntity);
    AuthenticationResponseDto convertEntityToResponseDto(UserEntity userEntity);
    RegisterRequestDto convertEntityToRegisterDto(UserEntity userEntity);

    UserEntity convertRequestDtoToEntity(AuthenticationRequestDto authenticationRequestDto);
    UserEntity convertResponseDtoToEntity(AuthenticationResponseDto authenticationRequestDto);
    UserEntity convertRegisterDtoToEntity(RegisterRequestDto authenticationRequestDto);
}
