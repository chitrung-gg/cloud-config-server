package com.viettel.spring.cloud.server.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.viettel.spring.cloud.server.dto.applicationprofile.ApplicationProfileDto;
import com.viettel.spring.cloud.server.dto.applicationprofile.CreateApplicationProfileDto;
import com.viettel.spring.cloud.server.dto.applicationprofile.UpdateApplicationProfileDto;
import com.viettel.spring.cloud.server.entity.ApplicationProfileEntity;

@Mapper(componentModel = "spring", uses = {ApplicationMapper.class})
public interface ApplicationProfileMapper {
    ApplicationProfileDto convertEntityToDto(ApplicationProfileEntity applicationProfileEntity);
    
    @Mapping(source = "application.id", target = "applicationId")
    CreateApplicationProfileDto convertEntityToCreateDto(ApplicationProfileEntity applicationProfileEntity);

    @Mapping(source = "application.id", target = "applicationId")
    UpdateApplicationProfileDto convertEntityToUpdateDto(ApplicationProfileEntity applicationProfileEntity);
    
    @Mapping(target = "configProperty", ignore = true)
    ApplicationProfileEntity convertDtoToEntity(ApplicationProfileDto applicationProfileDto);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "application", ignore = true)
    @Mapping(target = "configProperty", ignore = true)
    ApplicationProfileEntity convertCreateDtoToEntity(CreateApplicationProfileDto createApplicationProfileDto);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "application", ignore = true)
    @Mapping(target = "configProperty", ignore = true)
    ApplicationProfileEntity convertUpdateDtoToEntity(UpdateApplicationProfileDto updateApplicationProfileDto);
    
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "application", ignore = true)
    @Mapping(target = "configProperty", ignore = true)
    void updateEntityFromDto(UpdateApplicationProfileDto updateApplicationProfileDto, @MappingTarget ApplicationProfileEntity applicationProfileEntity);
}
