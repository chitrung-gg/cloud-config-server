package com.viettel.spring.cloud.server.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.viettel.spring.cloud.server.dto.user.CreateUserDto;
import com.viettel.spring.cloud.server.dto.user.UpdateUserDto;
import com.viettel.spring.cloud.server.dto.user.UserDto;
import com.viettel.spring.cloud.server.entity.UserEntity;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto convertEntityToDto(UserEntity userEntity);
    UserEntity convertDtoToEntity(UserDto UserDto);
    
    CreateUserDto convertEntityToCreateDto(UserEntity UserEntity);

    UpdateUserDto convertEntityToUpdateDto(UserEntity UserEntity);
    
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    UserEntity convertCreateDtoToEntity(CreateUserDto createUserDto);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    UserEntity convertUpdateDtoToEntity(UpdateUserDto updateUserDto);
    
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromDto(UpdateUserDto updateUserDto, @MappingTarget UserEntity UserEntity);
}
