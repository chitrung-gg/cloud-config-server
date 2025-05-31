package com.viettel.spring.cloud.server.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.viettel.spring.cloud.server.dto.userpermission.CreateUserPermissionDto;
import com.viettel.spring.cloud.server.dto.userpermission.UpdateUserPermissionDto;
import com.viettel.spring.cloud.server.dto.userpermission.UserPermissionDto;
import com.viettel.spring.cloud.server.entity.UserPermissionEntity;

@Mapper(componentModel = "spring")
public interface UserPermissionMapper {
    UserPermissionDto convertEntityToDto(UserPermissionEntity userPermissionEntity);
    
    @Mapping(source = "applicationProfile.id", target = "applicationProfileId")
    @Mapping(source = "user.id", target = "userId")
    CreateUserPermissionDto convertEntityToCreateDto(UserPermissionEntity userPermissionEntity);
    
    @Mapping(source = "applicationProfile.id", target = "applicationProfileId")
    @Mapping(source = "user.id", target = "userId")
    UpdateUserPermissionDto convertEntityToUpdateDto(UserPermissionEntity UserPermissionEntity);
    
    @Mapping(target = "applicationProfile", ignore = true) 
    @Mapping(target = "user", ignore = true) 
    UserPermissionEntity convertDtoToEntity(UserPermissionDto UserPermissionDto);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "applicationProfile", ignore = true)
    @Mapping(target = "user", ignore = true) 
    UserPermissionEntity convertCreateDtoToEntity(CreateUserPermissionDto createUserPermissionDto);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "applicationProfile", ignore = true)
    @Mapping(target = "user", ignore = true) 
    UserPermissionEntity convertUpdateDtoToEntity(UpdateUserPermissionDto updateUserPermissionDto);
    
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "applicationProfile", ignore = true)
    @Mapping(target = "user", ignore = true) 
    void updateEntityFromDto(UpdateUserPermissionDto updateUserPermissionDto, @MappingTarget UserPermissionEntity UserPermissionEntity);
}
